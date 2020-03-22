package com.bobisonfire.integral.functions;

import static java.lang.Math.*;
public class TanProvider implements FunctionProvider {
    @Override
    public double getValue(double x) {
        return tan(x);
    }

    @Override
    public boolean integralExists(double a, double b) {
        return cos(a) != 0 && cos(b) != 0 &&
                abs(b - a) < PI &&
                (b - a) * (tan(b) - tan(a)) >= 0;
    }

    @Override
    public String getDescription() {
        return "tg(x)";
    }
}
