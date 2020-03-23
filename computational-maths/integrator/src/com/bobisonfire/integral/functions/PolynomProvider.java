package com.bobisonfire.integral.functions;

import static java.lang.Math.pow;
public class PolynomProvider extends FunctionProvider {
    @Override
    public double getLeftValue(double x) {
        return 4 * pow(x, 3) + 2 * pow(x, 2) + 5 * x + 1;
    }

    @Override
    public String getDescription() {
        return "4x^3 + 2x^2 + 5x + 1";
    }
}
