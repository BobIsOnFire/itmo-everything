#include "bmp_utils.h"

static uint64_t get_remainder(uint64_t width) {
    uint64_t rem = (width * 3) % 4;
    return rem ? (4 - rem) : 0;
}

void bmp_header_print(bmp_header header) {
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

bmp_header bmp_header_compose(image img) {
    bmp_header header = BMP_HEADER_EMPTY;

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

    header.biSizeImage = (img.width * 3 + get_remainder(img.width)) * img.height;
    header.bfSize = header.biSizeImage + header.bfOffBits;

    return header;
}

read_status bmp_header_sanity_check(bmp_header header) {
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

read_status from_bmp(FILE *in, image * const img) {
    if (img == NULL) return READ_IMAGE_NULL;

    bmp_header header = BMP_HEADER_EMPTY;
    if (!fread(&header, sizeof(header), 1, in)) return READ_IO_ERROR;

    read_status sc_status = bmp_header_sanity_check(header);
    if (sc_status != READ_OK) return sc_status;

    *img = image_create(header.biHeight, header.biWidth);

    uint64_t remainder = get_remainder(img->width);
    uint8_t spare[4] = {0};

    for (int64_t row = img->height - 1; row >= 0; row--) {
        uint64_t row_bits = fread(img->data + row * img->width, sizeof(pixel), img->width, in);
        uint64_t rem_bits = fread(spare, sizeof(uint8_t), remainder, in);
        if (!row_bits || (remainder && !rem_bits)) {
            free(img->data);
            return READ_IO_ERROR;
        }
    }

    return READ_OK;
}

write_status to_bmp(FILE *out, image const *img) {
    if (img == NULL) return WRITE_IMAGE_NULL;

    bmp_header header = bmp_header_compose(*img);
    if (!fwrite(&header, sizeof(header), 1, out)) return WRITE_IO_ERROR;

    uint64_t remainder = get_remainder(img->width);
    uint8_t spare[4] = {0};

    for (int64_t row = img->height - 1; row >= 0; row--) {
        if (
            !fwrite(img->data + row * img->width, sizeof(pixel), img->width, out) ||
            (remainder && !fwrite(spare, sizeof(uint8_t), remainder, out))
        ) return WRITE_IO_ERROR;
    }

    return WRITE_OK;
}
