package com.bobisonfire;

import java.util.function.IntToDoubleFunction;

public final class SeriesFunction {
    private static final int SOFT_BOUND = 100;
    private static final int HARD_BOUND = 10002;

    public static SeriesFunction getInstance(double expansionPoint, IntToDoubleFunction coefficientSupplier) {
        return new SeriesFunction(expansionPoint, coefficientSupplier);
    }

    public static SeriesFunction arctan() {
        return getInstance(0, num -> (num % 2 == 0 ? 0.0 : (num % 4 == 1 ? 1.0 : -1.0)) / (num == 0 ? 1 : num));
    }

    private final double expansionPoint;
    private final IntToDoubleFunction coefficientSupplier;

    /* Following launches of same function will be faster than the first */
    private final Double[] coefficientCache = new Double[HARD_BOUND];

    private SeriesFunction(double expansionPoint, IntToDoubleFunction coefficientSupplier) {
        this.expansionPoint = expansionPoint;
        this.coefficientSupplier = coefficientSupplier;
    }

    public double get(double x, double precision) {
        double result = this.coefficientSupplier.applyAsDouble(0);
        double prev = result;
        double term = 1;

        for (int i = 1; i < HARD_BOUND; i++) {
            prev = result;
            double coefficient = getCoefficient(i);
            term *= x - this.expansionPoint;
            result += coefficient * term;

            /*
             * Terms can be skipped - e.g., arctan(x) skips every even term
             * Precision checking relies on difference between two last sums - term skipping could mess it up
             */
            if (coefficient == 0.0) continue;

            /* Infinite numbers always fail precision check - need a fast exit point for diverging series */
            if (Double.isInfinite(result)) return result;

            /* Could happen if coefficient supplier is invalid - zero division, negative square root, etc. */
            if (Double.isNaN(result)) throw new ArithmeticException("Cannot find value for x = " + x);

            /* As long as i < SOFT_BOUND, try getting the most precise result possible */
            if (result == prev) return result;

            /* Average of two last sums tends to be more precise for alternating series - e.g., arctan(x) */
            if (i >= SOFT_BOUND && Math.abs(result - prev) < precision) return (result + prev) / 2;
        }

        return (result + prev) / 2;
    }

    private double getCoefficient(int num) {
        if (this.coefficientCache[num] == null)
            this.coefficientCache[num] = this.coefficientSupplier.applyAsDouble(num);
        return this.coefficientCache[num];
    }
}
