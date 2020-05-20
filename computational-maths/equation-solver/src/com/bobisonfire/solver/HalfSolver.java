package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

public class HalfSolver extends EquationSolver {
    private static final int MAX_ITERATIONS = 1000;

    public HalfSolver(Function function) {
        super(function);
    }

    @Override
    protected double findRootBetween(double a, double b, double precision) {
        if (getValue(a) == 0) return a;
        if (getValue(b) == 0) return b;

        double x1 = a;
        double x2 = b;

        int i = 0;
        while (i++ <= MAX_ITERATIONS && x2 - x1 > precision) {
            double c = (x1 + x2) / 2;
            if (getValue(c) == 0) return c;

            if (getValue(c) * getValue(x1) <= 0) x2 = c;
            else x1 = c;
        }

        return (x1 + x2) / 2;
    }
}
