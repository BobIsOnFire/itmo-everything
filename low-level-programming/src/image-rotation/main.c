#define _GNU_SOURCE
#include <sys/time.h>
#include <sys/resource.h>

#include "bmp_utils.h"
#include "image_transform.h"

long interval_ms(struct timeval start, struct timeval end) {
    return ((end.tv_sec - start.tv_sec) * 1000000L) + end.tv_usec - start.tv_usec;
}

int main(int argc, char **argv) {
    struct rusage r;
    struct timeval u_start, u_deser, u_trans, u_end;
    struct timeval r_start, r_deser, r_trans, r_end;
    char *mask_name;

    FILE *in, *out, *mask;
    struct image img, img_res, img_mask;
    enum read_status read;
    enum write_status write;
    enum effect_mode mode;

    if (argc < 4) {
        fputs("Usage: lab8 [rotate=<angle>|blur|dilate|erode|sepia|sepia_fast] input.bmp output.bmp\n", stderr);
        return 2;
    }

    if (!strncmp("rotate", argv[1], 6)) mode = M_ROTATE;
    else if (!strcmp("blur", argv[1])) mode = M_BLUR;
    else if (!strcmp("dilate", argv[1])) mode = M_DILATE;
    else if (!strcmp("erode", argv[1])) mode = M_ERODE;
    else if (!strcmp("sepia", argv[1])) mode = M_SEPIA;
    else if (!strcmp("sepia_fast", argv[1])) mode = M_SEPIA_FAST;
    else {
        fputs("Usage: lab6 [rotate=<angle>|blur|dilate|erode|sepia|sepia_fast] input.bmp output.bmp\n", stderr);
        return 2;
    }

    gettimeofday(&r_start, NULL);
    getrusage(RUSAGE_THREAD, &r);
    u_start = r.ru_utime;

    in = fopen(argv[2], "rb");
    out = fopen(argv[3], "wb");
    if (!in || !out) {
        perror("Cannot open file");
        return 1;
    }

    read = from_bmp(in, &img);
    if (read != READ_OK) {
        perror_read("Error deserializing BMP file", read);
        return 1;
    }

    mask_name = getenv("BMP_MASK");
    if (mask_name && mask_name[0] != 0) {
        printf("Applying mask from %s\n", mask_name);
        mask = fopen(mask_name, "rb");
        if (!mask) {
            perror("Cannot open file");
            return 1;
        }

        read = from_bmp(mask, &img_mask);
        if (read != READ_OK) {
            perror_read("Error deserializing BMP mask file", read);
            return 1;
        }
    }

    gettimeofday(&r_deser, NULL);
    getrusage(RUSAGE_THREAD, &r);
    u_deser = r.ru_utime;

    if (mask_name && mask_name[0] != 0) apply_mask(&img, img_mask);

    switch (mode) {
        case M_ROTATE:
        {
            int64_t angle;
            char *s_angle = argv[1] + 6;
            
            if (s_angle[0] == 0) angle = 90;
            else sscanf(s_angle + 1, "%ld", &angle);

            img_res = rotate(img, angle);
            break;
        }
        case M_SEPIA:
            img_res = sepia(img);
            break;
        case M_SEPIA_FAST:
            img_res = sepia_fast(img);
            break;
        default:
            img_res = morph_transform(img, mode);
            break;
    }

    gettimeofday(&r_trans, NULL);
    getrusage(RUSAGE_THREAD, &r);
    u_trans = r.ru_utime;

    write = to_bmp(out, &img_res);
    if (write != WRITE_OK) {
        perror_write("Error serializing BMP file", write);
        return 1;
    }

    if (fclose(in) || fclose(out)) {
        perror("Cannot close file");
        return 1;
    }

    gettimeofday(&r_end, NULL);
    getrusage(RUSAGE_THREAD, &r);
    u_end = r.ru_utime;

    printf("Deserialization: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_start, r_deser), interval_ms(u_start, u_deser));
    printf(" Transformation: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_deser, r_trans), interval_ms(u_deser, u_trans));
    printf("  Serialization: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_trans, r_end), interval_ms(u_trans, u_end));
    printf("          Total: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_start, r_end), interval_ms(u_start, u_end));

    return 0;
}
