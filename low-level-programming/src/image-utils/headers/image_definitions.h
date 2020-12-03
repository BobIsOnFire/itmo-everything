#ifndef _IMAGE_DEFINITIONS_H
#define _IMAGE_DEFINITIONS_H

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct _pixel {
    uint8_t b, g, r;
} pixel;

static const pixel PX_WHITE = {255, 255, 255};

typedef struct _image {
    uint64_t width, height;
    pixel *data;
    uint8_t *mask;
} image;

#define IMAGE_EMPTY {0, 0, NULL, NULL}

typedef enum _read_status {
    READ_OK,
    READ_IMAGE_NULL,
    READ_INVALID_SIGNATURE,
    READ_INVALID_BITS,
    READ_INVALID_COMPRESSION,
    READ_INVALID_HEADER,
    READ_IO_ERROR
} read_status;

typedef enum _write_status {
    WRITE_OK = 0,
    WRITE_IMAGE_NULL,
    WRITE_IO_ERROR
} write_status;

image image_create(uint64_t height, uint64_t width);

void perror_read(const char *msg, read_status status);
void perror_write(const char *msg, write_status status);

#endif
