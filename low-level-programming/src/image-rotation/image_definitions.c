#include "image_definitions.h"

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
