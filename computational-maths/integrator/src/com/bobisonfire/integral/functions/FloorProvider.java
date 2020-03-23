package com.bobisonfire.integral.functions;

import static java.lang.Math.*;

public class FloorProvider extends FunctionProvider {
    @Override
    public double getLeftValue(double x) {
        return floor(x);
    }

    @Override
    public double getRightValue(double x) {
        if (x == floor(x)) return x - 1;
        return floor(x);
    }

    @Override
    public double[] getIntervals(double a, double b) {
        int x1 = (int) floor(min(a, b));
        int x2 = (int) ceil(max(a, b));

        double[] result = new double[x2 - x1 + 1];
        for (int i = 1; i < result.length - 1; i++) result[i] = x1 + i;
        result[0] = min(a, b);
        result[result.length - 1] = max(a, b);
        return result;
    }

    @Override
    public String getDescription() {
        return "floor(x)";
    }
}
