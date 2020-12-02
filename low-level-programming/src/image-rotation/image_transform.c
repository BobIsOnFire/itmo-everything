#include "image_transform.h"

static void *memory_map(uint64_t size) {
    return mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);
}

static bool is_light(struct pixel const px) {
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

static void translate_rel(double * const tp_x, double * const tp_y, double const p_x, double const p_y, double const rad) {
    *tp_x = p_x * cos(rad) - p_y * sin(rad);
    *tp_y = p_x * sin(rad) + p_y * cos(rad);
}

static void calc_new_size(struct image * const result, struct image const source, double const rad) {
    double right = source.width / 2.0;
    double left = -right;
    double top = source.height / 2.0;
    double bottom = -top;

    double lt_x, lt_y, rt_x, rt_y, lb_x, lb_y, rb_x, rb_y;
    translate_rel(&lt_x, &lt_y, left, top, rad);
    translate_rel(&rt_x, &rt_y, right, top, rad);
    translate_rel(&lb_x, &lb_y, left, bottom, rad);
    translate_rel(&rb_x, &rb_y, right, bottom, rad);

    result->width  = (uint64_t) round(dmax4(lt_x, rt_x, lb_x, rb_x)) * 2;
    result->height = (uint64_t) round(dmax4(lt_y, rt_y, lb_y, rb_y)) * 2;
}

static void px_kernel(struct pixel *kernel, struct pixel const *data, uint64_t mid, uint64_t offset) {
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

static struct pixel px_avg(struct pixel const *kernel) {
    uint64_t i, sum_r = 0, sum_g = 0, sum_b = 0;
    
    for (i = 0; i < 9; i++) {
        sum_r += kernel[i].r;
        sum_g += kernel[i].g;
        sum_b += kernel[i].b;
    }

    return (struct pixel) {sum_b / 9, sum_g / 9, sum_r / 9};
}

static struct pixel px_min(struct pixel const *kernel) {
    uint64_t i;
    struct pixel min = {UINT8_MAX, UINT8_MAX, UINT8_MAX};
    
    for (i = 0; i < 9; i++) {
        if (min.r > kernel[i].r) min.r = kernel[i].r;
        if (min.g > kernel[i].g) min.g = kernel[i].g;
        if (min.b > kernel[i].b) min.b = kernel[i].b;
    }

    return min;
}

static struct pixel px_max(struct pixel const *kernel) {
    uint64_t i;
    struct pixel max = {0, 0, 0};
    
    for (i = 0; i < 9; i++) {
        if (max.r < kernel[i].r) max.r = kernel[i].r;
        if (max.g < kernel[i].g) max.g = kernel[i].g;
        if (max.b < kernel[i].b) max.b = kernel[i].b;
    }

    return max;
}

static uint8_t sat(uint64_t x) {
    if (x < 256) return x;
    return 255;
}

static struct pixel px_sepia(struct pixel const *px) {
    struct pixel sepia, old = *px;
    sepia.b = sat(old.b * sepia_consts[0][0] + old.g * sepia_consts[0][1] + old.r * sepia_consts[0][2]);
    sepia.g = sat(old.b * sepia_consts[1][0] + old.g * sepia_consts[1][1] + old.r * sepia_consts[1][2]);
    sepia.r = sat(old.b * sepia_consts[2][0] + old.g * sepia_consts[2][1] + old.r * sepia_consts[2][2]);
    return sepia;
}

static void copy(struct image const *source, struct image const *result, uint64_t index) {
    result->data[index] = source->data[index];
}

void apply_mask(struct image * const source, struct image const mask) {
    double m_start_x, m_start_y, m_per_px;
    uint64_t row, column;
    
    source->mask = memory_map(source->height * source->width);
    m_per_px = dmax2((double) source->height / mask.height, (double) source->width / mask.width);

    m_start_x = (mask.width - source->width / m_per_px) / 2;
    m_start_y = (mask.height - source->height / m_per_px) / 2;

    for (row = 0; row < source->height; row++) {
        for (column = 0; column < source->width; column++) {
            uint64_t m_x = (uint64_t) floor(m_start_x + column / m_per_px);
            uint64_t m_y = (uint64_t) floor(m_start_y + row / m_per_px);
            source->mask[row * source->width + column] = is_light(mask.data[m_y * mask.width + m_x]);
        }
    }
}

struct image rotate(struct image const source, int64_t angle) {
    double rad = angle * M_PI / 180;
    double pivot_x = source.width / 2.0;
    double pivot_y = source.height / 2.0;
    double new_pivot_x, new_pivot_y;
    uint64_t x, y;
    
    struct image res;
    calc_new_size(&res, source, rad);
    res.data = memory_map(res.width * res.height * 3);

    new_pivot_x = res.width / 2.0;
    new_pivot_y = res.height / 2.0;

    for (y = 0; y < res.height; y++) {
        for (x = 0; x < res.width; x++) {
            double tp_x, tp_y, t_x, t_y;
            translate_rel(&tp_x, &tp_y, x - new_pivot_x, y - new_pivot_y, -rad);
            t_x = round(tp_x + pivot_x);
            t_y = round(tp_y + pivot_y);
            if (t_x < 0 || t_x > source.width - 1 || t_y < 0 || t_y > source.height - 1)
                res.data[y * res.width + x] = (struct pixel) {255, 255, 255};
            else
                res.data[y * res.width + x] = source.data[(uint64_t) t_y * source.width + (uint64_t) t_x];
        }
    }

    return res;
}

struct image morph_transform(struct image const source, enum effect_mode const mode) {
    struct image res;
    uint64_t row, column, i;
    struct pixel kernel[9];
    struct pixel (*px_morph)(struct pixel const *kernel);

    switch(mode) {
        case M_BLUR:
            px_morph = &px_avg;
            break;
        case M_DILATE:
            px_morph = &px_min;
            break;
        case M_ERODE:
            px_morph = &px_max;
            break;
        default:
            return source;
    }

    res.width = source.width;
    res.height = source.height;
    res.data = memory_map(res.width * res.height * 3);
    res.mask = source.mask;

    for (row = 0; row < res.height; row++) {
        copy(&source, &res, row * res.width);
        copy(&source, &res, (row + 1) * res.width - 1);
    }

    for (column = 0; column < res.width; column++) {
        copy(&source, &res, column);
        copy(&source, &res, res.height * res.width - column - 1);
    }

    for (i = 1; i < (res.height - 1) * (res.width - 1); i++) {
        if (res.mask && !res.mask[i]) {
            res.data[i] = source.data[i];
        } else {
            px_kernel(kernel, source.data, i, source.height);
            res.data[i] = px_morph(kernel);
        }
    }

    return res;
}

struct image blur(struct image const source) { return morph_transform(source, M_BLUR); }
struct image dilate(struct image const source) { return morph_transform(source, M_DILATE); }
struct image erode(struct image const source) { return morph_transform(source, M_ERODE); }

struct image sepia(struct image const source) {
    struct image res;
    uint64_t i;
    res.height = source.height;
    res.width = source.width;
    res.data = memory_map(res.width * res.height * 3);
    res.mask = source.mask;

    for (i = 0; i < source.height * source.width; i++) {
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

struct image sepia_fast(struct image const source) {
    struct image res;
    struct pixel *data = source.data;
    uint8_t *g_result;
    uint64_t blocks_count = source.height * source.width / 4;
    uint64_t i;

    res.width = source.width;
    res.height = source.height;
    res.data = memory_map(res.width * res.height * 3);
    res.mask = source.mask;
    g_result = (uint8_t*) res.data;

    for (i = 0; i < blocks_count; i++) {
        uint64_t j;
        for (j = 0; j < 12; j += 3) {
            flat_img[j] = flat_img[j + 1] = flat_img[j + 2] = data->b;
            flat_img[j + 12] = flat_img[j + 13] = flat_img[j + 14] = data->g;
            flat_img[j + 24] = flat_img[j + 25] = flat_img[j + 26] = data->r;
            data++;
        }
        
        packed_mul(flat_result, flat_img, flat_consts);
        for (j = 0; j < 12; j++) *g_result++ = sat( (uint64_t) roundf(flat_result[j]) );
    }

    for (i = blocks_count * 4; i < source.height * source.width; i++) {
        res.data[i] = px_sepia(&source.data[i]);
    }

    return res;
}
