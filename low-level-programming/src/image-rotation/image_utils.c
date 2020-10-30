#include "image_utils.h"

void perror_read(const char *msg, enum read_status status) {
    if (status == READ_OK) return;
    char *err_msg;

    switch (status) {
        case READ_IMAGE_NULL:
            err_msg = "Image pointer is NULL."; break;
        case READ_INVALID_SIGNATURE:
            err_msg = "Invalid signature - is input a BMP format?"; break;
        case READ_INVALID_BITS:
            err_msg = "Invalid bit count - only 24-bit images are supported."; break;
        case READ_INVALID_COMPRESSION:
            err_msg = "Invalid compression value - only uncompressed images are supported."; break;
        case READ_INVALID_HEADER:
            err_msg = "Invalid header values - BMP sanity check has not passed."; break;
        case READ_IO_ERROR:
            err_msg = "Failed while reading from input file."; break;
        default:
            err_msg = "Unknown failure"; break;
    }

    fprintf(stderr, "%s: %s\n", msg, err_msg);
}

void perror_write(const char *msg, enum write_status status) {
    if (status == WRITE_OK) return;
    char *err_msg;

    switch (status) {
        case WRITE_IMAGE_NULL:
            err_msg = "Image pointer is NULL."; break;
        case WRITE_IO_ERROR:
            err_msg = "Failed while writing into output file."; break;
        default:
            err_msg = "Unknown failure"; break;
    }

    fprintf(stderr, "%s: %s\n", msg, err_msg);
}

void bmp_header_print(struct bmp_header header) {
    printf("         bfType: %#x\n", header.bfType);
    printf("         bfSize: %u\n", header.bfSize);
    printf("     bfReserved: %u\n", header.bfReserved);
    printf("      bfOffBits: %u\n", header.bfOffBits);
    printf("         biSize: %u\n", header.biSize);
    printf("        biWidth: %u\n", header.biWidth);
    printf("       biHeight: %u\n", header.biHeight);
    printf("       biPlanes: %u\n", header.biPlanes);
    printf("     biBitCount: %u\n", header.biBitCount);
    printf("  biCompression: %u\n", header.biCompression);
    printf("    biSizeImage: %u\n", header.biSizeImage);
    printf("biXPelsPerMeter: %u\n", header.biXPelsPerMeter);
    printf("biYPelsPerMeter: %u\n", header.biYPelsPerMeter);
    printf("      biClrUsed: %u\n", header.biClrUsed);
    printf(" biClrImportant: %u\n", header.biClrImportant);
}

struct bmp_header bmp_header_compose(struct image img) {
    struct bmp_header header;
    uint64_t remainder;

    header.bfType          = BF_TYPE_DEFAULT;
    header.bfReserved      = BF_RESERVED_DEFAULT;
    header.bfOffBits       = BF_OFF_BITS_DEFAULT;
    header.biSize          = BI_SIZE_DEFAULT;
    header.biPlanes        = BI_PLANES_DEFAULT;

    header.biBitCount      = BI_BIT_COUNT_DEFAULT;
    header.biCompression   = BI_COMPRESSION_DEFAULT;
    header.biXPelsPerMeter = BI_X_PELS_PER_METER_DEFAULT;
    header.biYPelsPerMeter = BI_Y_PELS_PER_METER_DEFAULT;
    header.biClrUsed       = BI_CLR_USED_DEFAULT;
    header.biClrImportant  = BI_CLR_IMPORTANT_DEFAULT;

    header.biHeight = img.height;
    header.biWidth = img.width;

    remainder = (img.width * 3) % 4;
    remainder = (remainder == 0) ? 0 : (4 - remainder);
    header.biSizeImage = (img.width * 3 + remainder) * img.height;
    header.bfSize = header.biSizeImage + header.bfOffBits;

    return header;
}

enum read_status bmp_header_sanity_check(struct bmp_header header) {
    /* these do not make header invalid, but separate supported files from unsupported */
    if (header.bfType        != BF_TYPE_DEFAULT)        return READ_INVALID_SIGNATURE;
    if (header.biBitCount    != BI_BIT_COUNT_DEFAULT)   return READ_INVALID_BITS;
    if (header.biCompression != BI_COMPRESSION_DEFAULT) return READ_INVALID_COMPRESSION;

    /* these are mostly because of bad header composition */
    if (header.bfReserved    != BF_RESERVED_DEFAULT)    return READ_INVALID_HEADER;
    if (header.bfOffBits     != BF_OFF_BITS_DEFAULT)    return READ_INVALID_HEADER;
    if (header.biSize        != BI_SIZE_DEFAULT)        return READ_INVALID_HEADER;
    if (header.biPlanes      != BI_PLANES_DEFAULT)      return READ_INVALID_HEADER;

    return READ_OK;
}

enum read_status from_bmp(FILE *in, struct image * const image) {
    enum read_status sanity_check_status;
    uint8_t spare[4];
    int64_t remainder, row;
    struct bmp_header header;

    if (image == NULL) return READ_IMAGE_NULL;
    if (!fread(&header, sizeof(header), 1, in)) return READ_IO_ERROR;

    sanity_check_status = bmp_header_sanity_check(header);
    if (sanity_check_status != READ_OK) return sanity_check_status;

    image->height = header.biHeight;
    image->width = header.biWidth;    
    image->data = malloc(image->height * image->width * 3);

    /* how many bytes to skip to get on the next row */
    remainder = (image->width * 3) % 4;
    remainder = (remainder == 0) ? 0 : (4 - remainder);

    for (row = image->height - 1; row >= 0; row--) {
        uint64_t row_bits = fread(&image->data[row * image->width], sizeof(struct pixel), image->width, in);
        uint64_t rem_bits = fread(spare, sizeof(uint8_t), remainder, in);
        if (!row_bits || (remainder && !rem_bits)) {
            free(image->data);
            return READ_IO_ERROR;
        }
    }

    return READ_OK;
}

enum write_status to_bmp(FILE *out, struct image const *image) {
    int64_t remainder, row;
    const uint8_t spare[4] = {0};
    struct bmp_header header;
    
    if (image == NULL) return WRITE_IMAGE_NULL;
    header = bmp_header_compose(*image);
    if (!fwrite(&header, sizeof(header), 1, out)) return WRITE_IO_ERROR;

    /* how many bytes to skip to get on the next row */
    remainder = (image->width * 3) % 4;
    remainder = (remainder == 0) ? 0 : (4 - remainder);

    for (row = image->height - 1; row >= 0; row--) {
        uint64_t row_bits = fwrite(&image->data[row * image->width], sizeof(struct pixel), image->width, out);
        uint64_t rem_bits = fwrite(spare, sizeof(uint8_t), remainder, out);
        if (!row_bits || (remainder && !rem_bits)) return WRITE_IO_ERROR;
    }

    return WRITE_OK;
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

struct image rotate(struct image const source, int64_t angle) {
    double rad = angle * M_PI / 180;
    double pivot_x = source.width / 2.0;
    double pivot_y = source.height / 2.0;
    double new_pivot_x, new_pivot_y;
    uint64_t x, y;
    
    struct image res;
    calc_new_size(&res, source, rad);
    res.data = malloc(res.width * res.height * 3);

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

struct image morph_transform(struct image const source, enum effect_mode const mode) {
    struct image res;
    uint64_t row, column;
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
    res.data = malloc(res.width * res.height * 3);

    for (row = 0; row < res.height; row++) {
        res.data[row * res.width] = source.data[row * res.width];
        res.data[(row + 1) * res.width - 1] = source.data[(row + 1) * res.width - 1];
    }

    for (column = 0; column < res.width; column++) {
        res.data[column] = source.data[column];
        res.data[res.height * res.width - column - 1] = source.data[res.height * res.width - column - 1];
    }

    for (row = 1; row < res.height - 1; row++) {
        for (column = 1; column < res.width - 1; column++) {
            px_kernel(kernel, source.data, row * source.height + column, source.height);
            res.data[row * res.height + column] = px_morph(kernel);
        }
    }

    return res;
}

struct image blur(struct image const source) { return morph_transform(source, M_BLUR); }
struct image dilate(struct image const source) { return morph_transform(source, M_DILATE); }
struct image erode(struct image const source) { return morph_transform(source, M_ERODE); }
