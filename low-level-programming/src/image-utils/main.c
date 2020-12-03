#define _GNU_SOURCE
#include <sys/time.h>
#include <sys/resource.h>

#include "bmp_utils.h"
#include "image_transform.h"

static struct timeval r_start, r_deser, r_trans, r_end;
static struct timeval u_start, u_deser, u_trans, u_end;

static void save_time(struct timeval *real, struct timeval *user) {
    struct rusage r;
    gettimeofday(real, NULL);
    getrusage(RUSAGE_THREAD, &r);
    *user = r.ru_utime;
}

static long interval_ms(struct timeval start, struct timeval end) {
    return ((end.tv_sec - start.tv_sec) * 1000000L) + end.tv_usec - start.tv_usec;
}

static void print_resources() {
    printf("Deserialization: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_start, r_deser), interval_ms(u_start, u_deser));
    printf(" Transformation: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_deser, r_trans), interval_ms(u_deser, u_trans));
    printf("  Serialization: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_trans, r_end), interval_ms(u_trans, u_end));
    printf("          Total: real %ld μs\n"
           "                 user %ld μs\n", interval_ms(r_start, r_end), interval_ms(u_start, u_end));
}

static effect_mode try_mode(const char *modename) {
    if (!strncmp("rotate", modename, 6)) return M_ROTATE;
    if (!strcmp("blur", modename)) return M_BLUR;
    if (!strcmp("dilate", modename)) return M_DILATE;
    if (!strcmp("erode", modename)) return M_ERODE;
    if (!strcmp("sepia", modename)) return M_SEPIA;
    if (!strcmp("sepia_fast", modename)) return M_SEPIA_FAST;
    return M_INVALID;
}

static int try_bmp_read(const char *filename, image * const img) {
    FILE *in = fopen(filename, "rb");
    if (!in) {
        perror("Cannot open input file");
        return 1;
    }

    read_status status = from_bmp(in, img);
    if (status != READ_OK) {
        perror_read("Error deserializing BMP file", status);
        return 1;
    }

    if (fclose(in)) {
        perror("Cannot close input file");
        return 1;
    }

    return 0;
}

static int try_bmp_mask(image * const img_mask, image * const img) {
    char *mask_name = getenv("BMP_MASK");
    if (!mask_name || !mask_name[0]) {
        return 0;
    }
    
    printf("Applying mask from %s\n", mask_name);

    FILE* mask = fopen(mask_name, "rb");
    if (!mask) {
        perror("Cannot open file");
        return 1;
    }

    read_status status = from_bmp(mask, img_mask);
    if (status != READ_OK) {
        perror_read("Error deserializing BMP mask file", status);
        return 1;
    }
    
    if (fclose(mask)) {
        perror("Cannot close mask file");
        return 1;
    }

    apply_mask(img, *img_mask);
    return 0;
}

static int try_bmp_write(const char *filename, image const * const img) {
    FILE *out = fopen(filename, "wb");
    if (!out) {
        perror("Cannot open output file");
        return 1;
    }

    write_status status = to_bmp(out, img);
    if (status != WRITE_OK) {
        perror_read("Error serializing BMP file", status);
        return 1;
    }

    if (fclose(out)) {
        perror("Cannot close output file");
        return 1;
    }

    return 0;
}

static void free_img(image const img) {
    if (img.data) free(img.data);
    if (img.mask) free(img.mask);
}

int main(int argc, char **argv) {
    image img = IMAGE_EMPTY, img_res = IMAGE_EMPTY, img_mask = IMAGE_EMPTY;

    if (argc < 4) {
        fputs("Usage: lab8 [rotate=<angle>|blur|dilate|erode|sepia|sepia_fast] input.bmp output.bmp\n", stderr);
        return 2;
    }

    effect_mode mode = try_mode(argv[1]);
    if (mode == M_INVALID) {
        fputs("Usage: lab8 [rotate=<angle>|blur|dilate|erode|sepia|sepia_fast] input.bmp output.bmp\n", stderr);
        return 2;
    }

    save_time(&r_start, &u_start);

    if (try_bmp_read(argv[2], &img)) return 1;
    if (try_bmp_mask(&img_mask, &img)) return 1;

    save_time(&r_deser, &u_deser);

    switch (mode) {
        case M_ROTATE:
        {
            int64_t angle;
            char *s_angle = argv[1] + 6;
            
            if (! *s_angle) angle = 90;
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

    save_time(&r_trans, &u_trans);

    try_bmp_write(argv[3], &img_res);
    free_img(img);
    free_img(img_mask);
    free_img(img_res);

    save_time(&r_end, &u_end);
    print_resources();

    return 0;
}
