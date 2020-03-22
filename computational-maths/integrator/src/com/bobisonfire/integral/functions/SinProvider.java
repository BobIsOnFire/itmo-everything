package com.bobisonfire.integral.functions;

public class SinProvider implements FunctionProvider {
    @Override
    public double getValue(double x) {
        return Math.sin(x);
    }

    @Override
    public boolean integralExists(double a, double b) {
        return true;
    }

    @Override
    public String getDescription() {
        return "sin(x)";
    }
}
