#include <stdio.h>
#include <errno.h>
#include <limits.h>
#include <stdlib.h>

/* 
 * Scalar product function that is conscious of type overflow,
 * null pointers and arrays of different sizes.
 */

const int a[] = {1, -2, 3};
const int b[] = {4, 5, 6};

int mul(int a, int b) {
   	int sign = 1;

    if (a == 0 || b == 0) return 0;
    if (a < 0) {
        a = -a;
        sign = -sign;
    }

    if (b < 0) {
        b = -b;
        sign = -sign;
    }

    if (INT_MAX / b < a) {
        errno = EOVERFLOW;
        return (sign > 0) ? INT_MAX : INT_MIN;
    }

    return sign * a * b;
}

int add(int a, int b) {
    if (b > 0 && INT_MAX - b < a) {
        errno = EOVERFLOW;
        return INT_MAX;
    }

    if (b < 0 && INT_MIN - b > a) {
        errno = EOVERFLOW;
        return INT_MIN;
    }

    return a + b;
}

int scalar_product(const int * vec1, const int * vec2, size_t count) {
    size_t i;
    int product = 0;

    if (!vec1 || !vec2) {
        errno = EINVAL;
        return -1;
    }

    for ( i = 0; i < count; i++) {
        product = add(product, mul(vec1[i], vec2[i]));
        if (errno) return -1;
    }
    return product;
}

int main( int argc, char** argv ) {
    int result;
    size_t a_size = sizeof(a) / sizeof( a[0] );
    size_t b_size = sizeof(b) / sizeof( b[0] );

    if (a_size != b_size) {
        fputs("error: Given vectors are not of the same size.", stderr);
        return EXIT_FAILURE;
    }

    errno = 0;
    result = scalar_product(a, b, a_size);
    if (errno) {
        fputs("scalar_product() error: ", stderr);
        switch(errno) {
            case EINVAL:
                fputs("One of vector arguments is a null pointer.\n", stderr);
                break;
            case EOVERFLOW:
                fputs("Product value overflowed.\n", stderr);
                break;
            default:
                perror("");
                break;
        }
        return EXIT_FAILURE;
    }

    printf("The scalar product is: %d\n", result);
    return EXIT_SUCCESS;
}
