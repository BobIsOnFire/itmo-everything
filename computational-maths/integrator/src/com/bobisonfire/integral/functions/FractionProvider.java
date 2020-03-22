package com.bobisonfire.integral.functions;

public class FractionProvider implements FunctionProvider {
    @Override
    public double getValue(double x) {
        return 1 / x;
    }

    @Override
    public boolean integralExists(double a, double b) {
        return a * b > 0;
    }

    @Override
    public String getDescription() {
        return "1 / x";
    }
}
