package com.bobisonfire.functions;

import static java.lang.Math.*;
public class FractionProvider extends FunctionProvider {
    @Override
    public double getLeftValue(double x) {
        if (x == 0) return 1 / EPSILON;
        return 1 / x;
    }

    @Override
    public double getRightValue(double x) {
        if (x == 0) return -1 / EPSILON;
        return 1 / x;
    }

    @Override
    public double[] getIntervals(double a, double b) {
        if (!integralExists(a, b)) return null;
        double x1 = abs(a);
        double x2 = abs(b);
        return new double[] {min(x1, x2), max(x1, x2)};
    }

    @Override
    public boolean integralExists(double a, double b) {
        return a * b != 0;
    }

    @Override
    public String getDescription() {
        return "1 / x";
    }
}
