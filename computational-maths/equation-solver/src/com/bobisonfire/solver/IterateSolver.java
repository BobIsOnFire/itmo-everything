package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

public class IterateSolver extends EquationSolver {
    private static final int MAX_ITERATIONS = 1000;

    public IterateSolver(Function function) {
        super(function);
    }

    @Override
    protected double findRootBetween(double a, double b, double precision) {
        if (getValue(a) == 0) return a;
        if (getValue(b) == 0) return b;

        double max = Math.max(getDerivativeValue(a), getDerivativeValue(b));
        if (max == 0) max = Math.max(getDerivativeValue(a + 0.5), getDerivativeValue(b - 0.5));
        double lambda = -1 / max;

        double prevX = a;
        double x = prevX + lambda * getValue(prevX);

        int i = 0;
        while (i++ <= MAX_ITERATIONS && Math.abs(x - prevX) > precision) {
            prevX = x;
            x = prevX + lambda * getValue(prevX);
        }

        return x;
    }
}
