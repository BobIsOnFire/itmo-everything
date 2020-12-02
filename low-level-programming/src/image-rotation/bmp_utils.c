#include "bmp_utils.h"

static void *memory_map(uint64_t size) {
    return mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);
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
    image->data = memory_map(image->height * image->width * 3);
    image->mask = NULL;

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
