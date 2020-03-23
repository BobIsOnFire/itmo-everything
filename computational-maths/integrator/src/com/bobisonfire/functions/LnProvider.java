package com.bobisonfire.functions;

public class LnProvider extends FunctionProvider {
    @Override
    public double getLeftValue(double x) {
        if (x == 0) return Math.log(EPSILON);
        return Math.log(x);
    }

    @Override
    public double getRightValue(double x) {
        return Math.log(x);
    }

    @Override
    public boolean integralExists(double a, double b) {
        return a >= 0 && b >= 0;
    }

    @Override
    public String getDescription() {
        return "ln(x)";
    }
}
