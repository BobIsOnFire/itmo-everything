package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

public class NewtonPolynom implements Function {
    private static final double OUTPUT_PRECISION = 1E-2;

    private final double[] points;
    private final double[] constants;

    public NewtonPolynom(double[] points, double[] constants) {
        this.points = points;
        this.constants = constants;
    }

    public static Function from(double[] points, double[] constants) {
        if (points.length != constants.length) throw new SolverException("Points and constants do not correspond.");
        return new NewtonPolynom(points, constants);
    }

    @Override
    public double getValue(double x) {
        double value = 0;
        double multiplier = 1;

        for (int i = 0; i < points.length; i++) {
            value += constants[i] * multiplier;
            multiplier *= x - points[i];
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder fullBuilder = new StringBuilder();
        StringBuilder multiplierBuilder = new StringBuilder();

        for (int i = 0; i < points.length; i++) {
            if (i != 0) fullBuilder.append(" + ");
            fullBuilder.append(format(constants[i])).append(multiplierBuilder);

            if (points[i] == 0) multiplierBuilder.append("x");
            else multiplierBuilder.append("(x - ").append(format(points[i])).append(")");
        }

        return fullBuilder.toString();
    }

    private String format(double value) {
        return String.valueOf(Math.round(value / OUTPUT_PRECISION) * OUTPUT_PRECISION);
    }
}
