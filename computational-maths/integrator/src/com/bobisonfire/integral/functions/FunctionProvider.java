package com.bobisonfire.integral.functions;

public abstract class FunctionProvider {
    protected static final double EPSILON = 1E-12;

    public abstract double getLeftValue(double x);
    public double getRightValue(double x) {
        return getLeftValue(x);
    }

    public boolean integralExists(double a, double b) {
        return true;
    }

    public double[] getIntervals(double a, double b) {
        if (!integralExists(a, b)) return null;
        return new double[] {a, b};
    }

    public abstract String getDescription();
}
