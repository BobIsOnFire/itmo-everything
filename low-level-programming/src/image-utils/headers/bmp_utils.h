#ifndef _BMP_UTILS_H
#define _BMP_UTILS_H

#include <string.h>

#include "image_definitions.h"

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

typedef struct __attribute__((packed)) _bmp_header {
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
} bmp_header;

#define BMP_HEADER_EMPTY {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}

void bmp_header_print(bmp_header header);
bmp_header bmp_header_compose(image img);
read_status bmp_header_sanity_check(bmp_header header);

read_status from_bmp(FILE *in, image * const image);
write_status to_bmp(FILE *out, image const *image);

#endif
