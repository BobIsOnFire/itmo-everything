package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

class NewtonPolynom implements Function {
    private static final double OUTPUT_PRECISION = 1E-2;

    double[] arguments;
    double[] constants;
    double[] diagonal;

    NewtonPolynom(double[] arguments, double[] constants, double[] diagonal) {
        this.arguments = arguments;
        this.constants = constants;
        this.diagonal = diagonal;
    }

    @Override
    public double getValue(double x) {
        double value = 0;
        double multiplier = 1;

        for (int i = 0; i < arguments.length; i++) {
            value += constants[i] * multiplier;
            multiplier *= x - arguments[i];
        }

        return value;
    }

    @Override
    public String toString() {
        StringBuilder fullBuilder = new StringBuilder();
        StringBuilder multiplierBuilder = new StringBuilder();

        for (int i = 0; i < arguments.length; i++) {
            if (i != 0) fullBuilder.append(" + ");
            fullBuilder.append(format(constants[i])).append(multiplierBuilder);

            if (arguments[i] == 0) multiplierBuilder.append("x");
            else multiplierBuilder.append("(x - ").append(format(arguments[i])).append(")");
        }

        return fullBuilder.toString();
    }

    private String format(double value) {
        return String.valueOf(Math.round(value / OUTPUT_PRECISION) * OUTPUT_PRECISION);
    }
}
