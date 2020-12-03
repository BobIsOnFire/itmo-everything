#include "image_transform.h"

typedef struct _coord {
    double x, y;
} coord;

static bool is_light(pixel const px) {
    return px.r + px.g + px.b >= 128 * 3;
}

static double dmax2(double const v1, double const v2) {
    return (v1 > v2) ? v1 : v2;
}

static double dmax4(double const v1, double const v2, double const v3, double const v4) {
    double max = v1;
    if (v2 > max) max = v2;
    if (v3 > max) max = v3;
    if (v4 > max) max = v4;
    return max;
}

static coord dmax4_coord(coord const v1, coord const v2, coord const v3, coord const v4) {
    return (coord) {
        dmax4(v1.x, v2.x, v3.x, v4.x),
        dmax4(v1.y, v2.y, v3.y, v4.y)
    };
}

static coord translate_rel(coord const crd, double const rad) {
    return (coord) {
        crd.x * cos(rad) - crd.y * sin(rad),
        crd.x * sin(rad) + crd.y * cos(rad)
    };
}

static void calc_new_size(image * const result, image const source, double const rad) {
    double right = source.width / 2.0;
    double left = -right;
    double top = source.height / 2.0;
    double bottom = -top;

    coord translated = dmax4_coord(
        translate_rel((coord) {left, top}, rad),
        translate_rel((coord) {right, top}, rad),
        translate_rel((coord) {left, bottom}, rad),
        translate_rel((coord) {right, bottom}, rad)
    );

    result->width  = (uint64_t) round(translated.x) * 2;
    result->height = (uint64_t) round(translated.y) * 2;
}

static void px_kernel(pixel *kernel, pixel const *data, uint64_t mid, uint64_t offset) {
    kernel[0] = data[mid - offset - 1];
    kernel[1] = data[mid - offset];
    kernel[2] = data[mid - offset + 1];
    kernel[3] = data[mid - 1];
    kernel[4] = data[mid];
    kernel[5] = data[mid + 1];
    kernel[6] = data[mid + offset - 1];
    kernel[7] = data[mid + offset];
    kernel[8] = data[mid + offset + 1];
}

static pixel px_avg(pixel const *kernel) {
    uint64_t sum_r = 0, sum_g = 0, sum_b = 0;
    
    for (uint64_t i = 0; i < 9; i++) {
        sum_r += kernel[i].r;
        sum_g += kernel[i].g;
        sum_b += kernel[i].b;
    }

    return (pixel) {sum_b / 9, sum_g / 9, sum_r / 9};
}

static pixel px_min(pixel const *kernel) {
    pixel min = {UINT8_MAX, UINT8_MAX, UINT8_MAX};
    
    for (uint64_t i = 0; i < 9; i++) {
        if (min.r > kernel[i].r) min.r = kernel[i].r;
        if (min.g > kernel[i].g) min.g = kernel[i].g;
        if (min.b > kernel[i].b) min.b = kernel[i].b;
    }

    return min;
}

static pixel px_max(pixel const *kernel) {
    pixel max = {0, 0, 0};
    
    for (uint64_t i = 0; i < 9; i++) {
        if (max.r < kernel[i].r) max.r = kernel[i].r;
        if (max.g < kernel[i].g) max.g = kernel[i].g;
        if (max.b < kernel[i].b) max.b = kernel[i].b;
    }

    return max;
}

static uint8_t sat(uint64_t x) {
    return x < 256 ? x : 255;
}

static pixel px_sepia(pixel const *px) {
    return (pixel) {
        sat(px->b * sepia_consts[0][0] + px->g * sepia_consts[0][1] + px->r * sepia_consts[0][2]),
        sat(px->b * sepia_consts[1][0] + px->g * sepia_consts[1][1] + px->r * sepia_consts[1][2]),
        sat(px->b * sepia_consts[2][0] + px->g * sepia_consts[2][1] + px->r * sepia_consts[2][2])
    };
}

static void copy(image const *source, image const *result, uint64_t index) {
    result->data[index] = source->data[index];
}

static pixel (*px_morph[])(pixel const *kernel) = {
    [M_ROTATE] = NULL,
    [M_BLUR] = &px_avg,
    [M_DILATE] = &px_min,
    [M_ERODE] = &px_max,
    [M_SEPIA] = NULL,
    [M_SEPIA_FAST] = NULL
};

void apply_mask(image * const source, image const mask) {
    source->mask = malloc(source->height * source->width);
    double m_per_px = dmax2((double) source->height / mask.height, (double) source->width / mask.width);

    double m_start_x = (mask.width - source->width / m_per_px) / 2;
    double m_start_y = (mask.height - source->height / m_per_px) / 2;

    for (uint64_t row = 0; row < source->height; row++) {
        for (uint64_t column = 0; column < source->width; column++) {
            uint64_t m_x = (uint64_t) floor(m_start_x + column / m_per_px);
            uint64_t m_y = (uint64_t) floor(m_start_y + row / m_per_px);
            source->mask[row * source->width + column] = is_light(mask.data[m_y * mask.width + m_x]);
        }
    }
}

image rotate(image const source, int64_t angle) {
    double rad = angle * M_PI / 180;

    image res = {0};
    calc_new_size(&res, source, rad);
    res.data = malloc(res.width * res.height * 3);

    coord pivot = {source.width / 2.0, source.height / 2.0};
    coord new_pivot = {res.width / 2.0, res.height / 2.0};

    for (uint64_t y = 0; y < res.height; y++) {
        for (uint64_t x = 0; x < res.width; x++) {
            coord tp = translate_rel((coord) {x - new_pivot.x, y - new_pivot.y}, -rad);
            tp.x = round(tp.x + pivot.x);
            tp.y = round(tp.y + pivot.y);

            if (tp.x < 0 || tp.x > source.width - 1 || tp.y < 0 || tp.y > source.height - 1)
                res.data[y * res.width + x] = PX_WHITE;
            else
                res.data[y * res.width + x] = source.data[(uint64_t) tp.y * source.width + (uint64_t) tp.x];
        }
    }

    return res;
}

image morph_transform(image const source, effect_mode const mode) {
    if (!px_morph[mode]) return source;

    image res = image_create(source.width, source.height);
    res.mask = source.mask;

    for (uint64_t row = 0; row < res.height; row++) {
        copy(&source, &res, row * res.width);
        copy(&source, &res, (row + 1) * res.width - 1);
    }

    for (uint64_t column = 0; column < res.width; column++) {
        copy(&source, &res, column);
        copy(&source, &res, res.height * res.width - column - 1);
    }

    for (uint64_t i = 1; i < (res.height - 1) * (res.width - 1); i++) {
        if (res.mask && !res.mask[i]) {
            res.data[i] = source.data[i];
        } else {
            pixel kernel[9] = {0};
            px_kernel(kernel, source.data, i, source.height);
            res.data[i] = px_morph[mode](kernel);
        }
    }

    return res;
}

image blur(image const source) { return morph_transform(source, M_BLUR); }
image dilate(image const source) { return morph_transform(source, M_DILATE); }
image erode(image const source) { return morph_transform(source, M_ERODE); }

image sepia(image const source) {
    image res = image_create(source.height, source.width);
    res.mask = source.mask;

    for (uint64_t i = 0; i < source.height * source.width; i++) {
        if (res.mask && !res.mask[i]) {
            res.data[i] = source.data[i];
        } else {
            res.data[i] = px_sepia(&source.data[i]);
        }
    }

    return res;
}

static float flat_img[36];
static float flat_result[12];

image sepia_fast(image const source) {
    image res = image_create(source.height, source.width);
    res.mask = source.mask;

    pixel *data = source.data;
    uint8_t *g_result = (uint8_t*) res.data;

    uint64_t blocks_count = source.height * source.width / 4;
    for (uint64_t i = 0; i < blocks_count; i++) {
        for (uint64_t j = 0; j < 12; j += 3) {
            flat_img[j] = flat_img[j + 1] = flat_img[j + 2] = data->b;
            flat_img[j + 12] = flat_img[j + 13] = flat_img[j + 14] = data->g;
            flat_img[j + 24] = flat_img[j + 25] = flat_img[j + 26] = data->r;
            data++;
        }
        
        packed_mul(flat_result, flat_img, flat_consts);
        for (uint64_t j = 0; j < 12; j++) *g_result++ = sat( (uint64_t) roundf(flat_result[j]) );
    }

    for (uint64_t i = blocks_count * 4; i < source.height * source.width; i++) {
        res.data[i] = px_sepia(&source.data[i]);
    }

    return res;
}
