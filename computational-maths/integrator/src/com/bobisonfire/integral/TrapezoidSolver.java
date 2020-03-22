package com.bobisonfire.integral;

import com.bobisonfire.integral.functions.FunctionProvider;

import static java.lang.Math.*;

public class TrapezoidSolver {
    private final static int INITIAL_SEGMENT_AMOUNT = 10;
    private final static double THETA = 1.0 / 3;

    private FunctionProvider function;
    private boolean negated = false;

    public TrapezoidSolver(FunctionProvider function) {
        this.function = function;
    }

    public Solution integrate(double a, double b, double epsilon) {
        if (!function.integralExists(a, b)) throw new IntegralException("Invalid bounds - integral does not converge.");

        double x1 = a;
        double x2 = b;

        if (x1 > x2) {
            double t = x1;
            x1 = x2;
            x2 = t;
            negated = true;
        }

        int segments = INITIAL_SEGMENT_AMOUNT;
        double baseIntegral;
        double currentIntegral = getApproximateIntegral(x1, x2, segments);
        double error;
        do {
            segments *= 2;
            baseIntegral = currentIntegral;
            currentIntegral = getApproximateIntegral(x1, x2, segments);
            error = THETA * abs(baseIntegral - currentIntegral);
        } while (error >= epsilon);

        if (negated) currentIntegral *= -1;

        return SolutionBuilder.instance()
                .integral(currentIntegral)
                .segments(segments)
                .error(error)
                .get();
    }

    private double getApproximateIntegral(double a, double b, int segments) {
        double h = (b - a) / segments;
        double integral = 0;
        for (double x = a; x < b; x += h) integral += (function.getValue(x) + function.getValue(x + h)) * h / 2;

        return integral;
    }
}
