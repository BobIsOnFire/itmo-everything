package com.bobisonfire;

import static java.lang.Math.abs;

public class Utils {
    public static boolean arePrecise(double x, double y) {
        return arePrecise(x, y, SeriesFunction.DEFAULT_PRECISION);
    }

    public static boolean arePrecise(double x, double y, double precision) {
        return abs(x - y) < precision;
    }
}
