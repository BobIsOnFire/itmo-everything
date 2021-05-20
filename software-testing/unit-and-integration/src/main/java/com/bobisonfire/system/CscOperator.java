package com.bobisonfire.system;

import com.bobisonfire.Utils;

import static java.lang.Math.*;

public class CscOperator implements Csc {
    private final Cos cos;

    public CscOperator(Cos cos) {
        this.cos = cos;
    }

    @Override
    public double get(double x, double precision) {
        double value = cos.get(x, precision / 10);
        if (Utils.arePrecise(value, 1) || Utils.arePrecise(value, -1)) return Double.POSITIVE_INFINITY;

        double sin = sqrt(1 - pow( value, 2));

        if (x >= 0 && x % (2 * PI) > PI) sin *= -1;
        if (x < 0 && x % (2 * PI) > -PI) sin *= -1;
        return 1 / sin;
    }
}
