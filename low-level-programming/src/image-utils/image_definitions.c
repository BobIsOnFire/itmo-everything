#include "image_definitions.h"

static const char * rs_messages[] = {
    [READ_OK] = "OK",
    [READ_IMAGE_NULL] = "Image pointer is NULL.",
    [READ_INVALID_SIGNATURE] = "Invalid signature - is input a BMP format?",
    [READ_INVALID_BITS] = "Invalid bit count - only 24-bit images are supported.",
    [READ_INVALID_COMPRESSION] = "Invalid compression value - only uncompressed images are supported.",
    [READ_INVALID_HEADER] = "Invalid header values - BMP sanity check has not passed.",
    [READ_IO_ERROR] = "Failed while reading from input file."
};

static const char * ws_messages[] = {
    [WRITE_OK] = "OK",
    [WRITE_IMAGE_NULL] = "Image pointer is NULL.",
    [WRITE_IO_ERROR] = "Failed while writing into output file."
};

image image_create(uint64_t height, uint64_t width) {
    return (image) {
        width, height,
        malloc(width * height * sizeof(pixel)),
        NULL
    };
}

void perror_read(const char *msg, read_status status) {
    if (status == READ_OK) return;
    fprintf(stderr, "%s: %s\n", msg, rs_messages[status]);
}

void perror_write(const char *msg, write_status status) {
    if (status == WRITE_OK) return;
    fprintf(stderr, "%s: %s\n", msg, ws_messages[status]);
}
