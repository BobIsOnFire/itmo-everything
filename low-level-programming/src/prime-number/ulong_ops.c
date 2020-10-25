#include "ulong_ops.h"

ulong ugcd(ulong a, ulong b) {
    ulong tmp;
    
    while (b) {
        tmp = a % b;
        a = b;
        b = tmp;
    }

    return a;
}

ulong umulmod(ulong a, ulong b, ulong mod) {
    ulong result = 0;
    a = a % mod;

    for (;;) {
        if (b & 1) result = (result + a) % mod;
        b >>= 1;
        if (!b) break;
        a = (a * 2UL) % mod;
    }

    return result;
}

ulong upowmod(ulong base, ulong power, ulong mod) {
    ulong result = 1;
    for (;;)
    {
        if (power & 1) result = umulmod(result, base, mod);
        power >>= 1;
        if (!power) break;
        base = umulmod(base, base, mod);
    }

    return result;
}
