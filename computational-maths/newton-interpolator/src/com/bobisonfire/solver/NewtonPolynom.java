package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

class NewtonPolynom implements Function {
    private static final DecimalFormat df = new DecimalFormat("#");

    static {
        df.setMaximumFractionDigits(2);
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
            if (i != 0) fullBuilder.append(" + ");
            fullBuilder.append(df.format(constants[i])).append(multiplierBuilder);

            if (arguments[i] == 0) multiplierBuilder.append("x");
            else multiplierBuilder.append("(x - ").append(df.format(arguments[i])).append(")");
        }

        return fullBuilder.toString();
    }
}
