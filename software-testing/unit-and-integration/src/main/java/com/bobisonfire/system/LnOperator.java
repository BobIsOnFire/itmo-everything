package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;

public class LnOperator implements Ln {
    // ln(x) = ln((1 + y)/(1 - y)) = 2 * (y + y^3 / 3 + y^5 / 5 + ...)
    // converges for positive x
    private static final SeriesFunction LN_SERIES = SeriesFunction.getInstance(0,
            num -> num % 2 == 1 ? (2.0 / num) : 0.0);

    @Override
    public double get(double x, double precision) {
        if (x <= 0) return Double.NEGATIVE_INFINITY;
        if (x == Double.POSITIVE_INFINITY) return Double.POSITIVE_INFINITY;
        return LN_SERIES.get((x - 1)/(x + 1), precision);
    }
}
