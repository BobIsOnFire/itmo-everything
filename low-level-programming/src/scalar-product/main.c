#include <stdio.h>

/* 
 * Scalar product function that does only requested tasks,
 * copy of example in the book. Pretty elegant.
 */

const int a[] = {1, 2, 3};
const int b[] = {4, 5, 6};

int scalar_product(const int * vec1, const int * vec2, size_t count) {
    size_t i;
    int product = 0;

    for ( i = 0; i < count; i++)
        product += vec1[i] * vec2[i];

    return product;
}

int main( int argc, char** argv ) {
    printf(
        "The scalar product is: %d\n",
        scalar_product(a, b, sizeof(a) / sizeof( a[0] ))
    );
    return 0;
}
