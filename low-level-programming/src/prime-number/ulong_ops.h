#ifndef _ULONG_OPS_H_
#define _ULONG_OPS_H_

typedef unsigned int ulong;

/* 
 * Find the greatest common divisor using Euclidean algorithm.
 */
ulong ugcd(ulong a, ulong b);

/* 
 * Perform fast modular multiplication.
 * The implementation is aware of unsigned long overflow.
 * 
 * Return (a * b) % mod.
 */
ulong umulmod(ulong a, ulong b, ulong mod);

/* 
 * Perform fast modular exponential.
 * The implementation is aware of unsigned long overflow.
 * 
 * Return (base ^ power) % mod.
 */
ulong upowmod(ulong base, ulong power, ulong mod);

#endif