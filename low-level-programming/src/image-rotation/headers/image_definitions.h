#ifndef _IMAGE_DEFINITIONS_H
#define _IMAGE_DEFINITIONS_H

#include <stdint.h>
#include <stdio.h>

struct pixel {
    uint8_t b, g, r;
};

struct image {
    uint64_t width, height;
    struct pixel *data;
    uint8_t *mask;
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

void perror_read(const char *msg, enum read_status status);
void perror_write(const char *msg, enum write_status status);

#endif