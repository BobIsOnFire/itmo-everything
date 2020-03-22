package com.bobisonfire.integral.functions;

public class LnProvider implements FunctionProvider {
    @Override
    public double getValue(double x) {
        return Math.log(x);
    }

    @Override
    public boolean integralExists(double a, double b) {
        return a > 0 && b > 0;
    }

    @Override
    public String getDescription() {
        return "ln(x)";
    }
}
