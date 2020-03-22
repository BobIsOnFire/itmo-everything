package com.bobisonfire.integral.functions;

public interface FunctionProvider {
    double getValue(double x);
    boolean integralExists(double a, double b);
    String getDescription();
}
