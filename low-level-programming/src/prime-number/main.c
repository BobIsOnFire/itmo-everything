#include <stdio.h>
#include "ulong_ops.h"

/* 
 * Sieve of Eratosthenes filled up to 1000.
 * 
 * 0 char value stands for composite, 1 - for prime.
 * Numbers 0 and 1 themselves count as composite.
 * 
 * Came from magic NASM preprocessor.
 */
extern const char sieve[];

/*
 * Parameters for deterministic Miller-Rabin primality test.
 * 
 * It is proved to be enough to check all prime numbers up
 * to 37 to correctly test all numbers up to 2^64 (unsigned
 * long range).
 */
const ulong miller_parameters[] = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

/*
 * Iterate through first LIMIT numbers from Sieve of
 * Eratosthenes. With a big enough number (e.g. 1000, which
 * is the maximum possible in this implementation), sieve
 * iteration might be the best choice to find a composite
 * number (in almost 100% of the cases).
 * 
 * Return 0 if N is below LIMIT and is composite OR if N has
 * any divisor below LIMIT - only prime divisors are checked
 * for the sake of speed.
 * 
 * Return 1 if N is below LIMIT and is prime.
 * 
 * Return -1 in all other cases - N is over or equal to LIMIT
 * and has no divisors in the first LIMIT numbers. That means
 * other algorithm is needed to test primality.
 */
int trivial_sieve(ulong n, size_t limit);

/*
 * Test an odd N number for primality using deterministic Miller-Rabin
 * test.
 * 
 * Return 0 if N is composite and 1 if N is prime.
 */
int miller_rabin(ulong n);

/*
 * Provide one iteration for Miller-Rabin test.
 * 
 * Variable A is a test parameter. See "miller_parameters".
 * POWER and FACTOR are provided so that the following expression
 * is true:
 * 
 * N - 1 = 2 ^ POWER * FACTOR
 * 
 * Return 0 if N is composite, 1 if it is still not defined and
 * next parameter should be used. If there are no parameters left,
 * 1 means that N is prime.
 */
int base_miller_rabin(ulong n, ulong a, ulong power, ulong factor);

/* 
 * Test primality of N.
 * 
 * The tests consists of the combination of trivial sieve and
 * deterministic Miller-Rabin test. See corresponding methods for
 * details.
 * 
 * Return 0 if N is composite and 1 if N is prime.
 */
int is_prime(ulong n);

int main(void) {
    /* ulong n;
    scanf("%lu", &n);
    puts(is_prime(n) ? "prime" : "composite");
    */

    printf(5 + "dsfsfddfs\n\n");
    return 0;
}

int trivial_sieve(ulong n, size_t limit) {
    size_t i;
    if (n < limit) return sieve[n];

    for (i = 0; i < limit; i++) {        
        if (sieve[i] && n % i == 0) {
            return 0;
        }
    }

    return -1;
}

int miller_rabin(ulong n) {
    size_t len = sizeof(miller_parameters) / sizeof(miller_parameters[0]);
    size_t i;
    ulong power = 0;
    ulong factor = n - 1;
    
    while (!(factor & 1)) {
        ++power;
        factor >>= 1;
    }

    for (i = 0; i < len; i++) {
        if (base_miller_rabin(n, miller_parameters[i], power, factor)) {
            continue;
        } else {
            return 0;
        }
    }

    return 1;
}

int base_miller_rabin(ulong n, ulong a, ulong power, ulong factor) {
    ulong i, remainder;

    remainder = upowmod(a, factor, n);
    if (remainder == 1 || remainder == n - 1) return 1;

    for (i = 1; i < power; i++) {
        remainder = umulmod(remainder, remainder, n);
        if (remainder == n - 1) return 1;
    }

    return 0;
}

int is_prime(ulong n) {
    int trivial = trivial_sieve(n, 1000);
    if (trivial == -1) return miller_rabin(n);
    return trivial;
}
