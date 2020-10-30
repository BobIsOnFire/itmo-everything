#ifndef _IMAGE_ROT_H
#define _IMAGE_ROT_H

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

static const uint16_t BF_TYPE_DEFAULT = 0x4d42; /* BMP type, little-endian */
static const uint32_t BF_RESERVED_DEFAULT = 0;
static const uint32_t BF_OFF_BITS_DEFAULT = 54; /* total BMPv3 header size */
static const uint16_t BI_PLANES_DEFAULT = 1;
static const uint32_t BI_SIZE_DEFAULT = 40; /* total size of BMPv3 specific values */

/* Defaults specific for this task */
static const uint16_t BI_BIT_COUNT_DEFAULT = 24;
static const uint32_t BI_COMPRESSION_DEFAULT = 0;
static const uint32_t BI_X_PELS_PER_METER_DEFAULT = 0;
static const uint32_t BI_Y_PELS_PER_METER_DEFAULT = 0;
static const uint32_t BI_CLR_USED_DEFAULT = 0;
static const uint32_t BI_CLR_IMPORTANT_DEFAULT = 0;

struct __attribute__((packed)) bmp_header {
    uint16_t bfType;
    uint32_t bfSize;
    uint32_t bfReserved;
    uint32_t bfOffBits;
    uint32_t biSize;

    uint32_t biWidth;
    uint32_t biHeight;
    uint16_t biPlanes;
    uint16_t biBitCount;
    uint32_t biCompression;
    uint32_t biSizeImage;
    uint32_t biXPelsPerMeter;
    uint32_t biYPelsPerMeter;
    uint32_t biClrUsed;
    uint32_t biClrImportant;
};

struct pixel {
    uint8_t b, g, r;
};

struct image {
    uint64_t width, height;
    struct pixel *data;
};

enum read_status {
    READ_OK,
    READ_IMAGE_NULL,
    READ_INVALID_SIGNATURE,
    READ_INVALID_BITS,
    READ_INVALID_COMPRESSION,
    READ_INVALID_HEADER,
    READ_IO_ERROR
};

enum write_status {
    WRITE_OK = 0,
    WRITE_IMAGE_NULL,
    WRITE_IO_ERROR
};

enum effect_mode {
    M_ROTATE,
    M_BLUR,
    M_DILATE,
    M_ERODE
};


void perror_read(const char *msg, enum read_status status);
void perror_write(const char *msg, enum write_status status);

void bmp_header_print(struct bmp_header header);
struct bmp_header bmp_header_compose(struct image img);
enum read_status bmp_header_sanity_check(struct bmp_header header);

enum read_status from_bmp(FILE *in, struct image * const image);
enum write_status to_bmp(FILE *out, struct image const *image);

struct image rotate(struct image const source, int64_t angle);

struct image morph_transform(struct image const source, enum effect_mode const mode);
struct image blur(struct image const source);
struct image dilate(struct image const source);
struct image erode(struct image const source);

#endif
