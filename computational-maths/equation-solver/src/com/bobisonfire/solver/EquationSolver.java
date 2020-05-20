package com.bobisonfire.solver;

import com.bobisonfire.function.Function;
import com.bobisonfire.function.Variable;

import java.util.ArrayList;
import java.util.List;

public abstract class EquationSolver {
    private final Function function;
    private final Function derivative;

    public EquationSolver(Function function) {
        this.function = function;
        this.derivative = function.getDerivative(Variable.variableNames()[0]);
        Variable.clearPool();
    }

    public final Solution getSolution(double a, double b, double precision, double stepSize) {
        if (precision <= 0) throw new SolverException("Precision value is not valid: " + precision);
        double x1 = Math.min(a, b);
        double x2 = Math.max(a, b);

        List<Double> roots = new ArrayList<>();
        double prevRoot = x1 - x2;

        while (x1 <= x2 - stepSize) {
            if (getValue(x1) * getValue(x1 + stepSize) <= 0) {
                double root = findRootBetween(x1, x1 + stepSize, precision);
                if (Math.abs(root - prevRoot) > precision) {
                    roots.add(root);
                    prevRoot = root;
                }
            }
            x1 += stepSize;
        }

        double[] array = new double[roots.size()];
        int i = 0;
        for (Double root : roots) array[i++] = root;

        return new Solution(function, array);
    }

    protected final double getValue(double x) {
        return function.getValue(x);
    }

    protected final double getDerivativeValue(double x) {
        return derivative.getValue(x);
    }

    protected abstract double findRootBetween(double a, double b, double precision);
}
