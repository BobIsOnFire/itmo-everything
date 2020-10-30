#include "image_utils.h"
#include <string.h>

int main(int argc, char **argv) {
    FILE *in, *out;
    struct image img, img_res;
    enum read_status read;
    enum write_status write;
    enum effect_mode mode;

    if (argc < 4) {
        fputs("Usage: lab6 [rotate=<angle>|blur|dilate|erode] input.bmp output.bmp\n", stderr);
        return 2;
    }

    if (!strncmp("rotate", argv[1], 6)) mode = M_ROTATE;
    else if (!strcmp("blur", argv[1])) mode = M_BLUR;
    else if (!strcmp("dilate", argv[1])) mode = M_DILATE;
    else if (!strcmp("erode", argv[1])) mode = M_ERODE;
    else {
        fputs("Usage: lab6 [rotate|blur|dilate|erode] input.bmp output.bmp\n", stderr);
        return 2;
    }

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
    
    if (mode == M_ROTATE) {
        int64_t angle;
        char *s_angle = argv[1] + 6;
        
        if (s_angle[0] == 0) angle = 90;
        else sscanf(s_angle + 1, "%ld", &angle);

        img_res = rotate(img, angle);
    } else img_res = morph_transform(img, mode);

    write = to_bmp(out, &img_res);
    if (write != WRITE_OK) {
        perror_write("Error serializing BMP file", write);
        return 1;
    }

    if (fclose(in) || fclose(out)) {
        perror("Cannot close file");
        return 1;
    }

    free(img.data);
    free(img_res.data);
    return 0;
}
