package com.bobisonfire.integral;

import com.bobisonfire.integral.functions.FunctionProvider;

import static java.lang.Math.*;

public class TrapezoidSolver {
    private final static int INITIAL_SEGMENT_AMOUNT = 10;
    private final static double THETA = 1.0 / 3;

    private FunctionProvider function;

    public TrapezoidSolver(FunctionProvider function) {
        this.function = function;
    }

    public Solution integrate(double a, double b, double epsilon) {
        if (!function.integralExists(a, b)) throw new IntegralException("Invalid bounds - integral does not converge.");

        double x1 = min(a, b);
        double x2 = max(a, b);
        boolean negated = a > b;

        double[] intervalPoints = function.getIntervals(x1, x2);

        double integral = 0;
        double error = 0;
        int segments = 0;

        int intervalCount = intervalPoints.length - 1;
        for (int i = 0; i < intervalCount; i++) {
            Solution s = getPartialIntegral(intervalPoints[i], intervalPoints[i + 1], epsilon / intervalCount);
            integral += s.getIntegral();
            error += s.getError();
            segments += s.getSegments();
        }

        if (negated) integral *= -1;

        return SolutionBuilder.instance()
                .integral(integral)
                .segments(segments)
                .error(error)
                .get();
    }

    private Solution getPartialIntegral(double a, double b, double epsilon) {
        if (a == b)
            return SolutionBuilder.instance()
                    .integral(0)
                    .segments(1)
                    .error(0)
                    .get();

        int segments = INITIAL_SEGMENT_AMOUNT;
        double baseIntegral;
        double currentIntegral = getTrapezoidsSquare(a, b, segments);
        double error;
        do {
            segments *= 2;
            baseIntegral = currentIntegral;
            currentIntegral = getTrapezoidsSquare(a, b, segments);
            error = THETA * abs(baseIntegral - currentIntegral);
        } while (error >= epsilon);

        return SolutionBuilder.instance()
                .integral(currentIntegral)
                .segments(segments)
                .error(error)
                .get();
    }

    private double getTrapezoidsSquare(double a, double b, int segments) {
        double h = (b - a) / segments;
        double integral = 0;
        for (double x = a; x < b; x += h) integral += (function.getLeftValue(x) + function.getRightValue(x + h)) * h / 2;

        return integral;
    }
}
