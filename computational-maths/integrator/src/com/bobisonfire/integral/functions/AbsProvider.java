package com.bobisonfire.integral.functions;

public class AbsProvider implements FunctionProvider {
    @Override
    public double getValue(double x) {
        return Math.abs(x);
    }

    @Override
    public boolean integralExists(double a, double b) {
        return true;
    }

    @Override
    public String getDescription() {
        return "|x|";
    }
}
