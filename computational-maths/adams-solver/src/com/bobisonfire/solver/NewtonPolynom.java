package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

class NewtonPolynom implements Function {
    private static final double OUTPUT_PRECISION = 1E-5;
    private static final DecimalFormat df = new DecimalFormat("#");

    static {
        df.setMaximumFractionDigits(5);
        df.setMinimumIntegerDigits(1);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    }

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
            if (Math.abs(constants[i]) >= OUTPUT_PRECISION) {
                if (fullBuilder.length() != 0) fullBuilder.append(constants[i] > 0 ? " + " : " - ");
                fullBuilder.append(df.format(Math.abs(constants[i]))).append(multiplierBuilder);
            }

            if (arguments[i] == 0) multiplierBuilder.append("x");
            else multiplierBuilder.append(arguments[i] > 0 ? "(x + " : "(x - ")
                        .append(df.format( Math.abs(arguments[i]) ))
                        .append(')');
        }

        return (fullBuilder.length() == 0) ? "0" : fullBuilder.toString();
    }
}
