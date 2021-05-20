package com.bobisonfire;

import java.util.function.IntToDoubleFunction;

public final class SeriesFunction {
    private static final int SOFT_BOUND = 100;

    public static final int DEFAULT_HARD_BOUND = 10002;
    public static final double DEFAULT_PRECISION = 1E-5;

    public static SeriesFunction getInstance(double expansionPoint, IntToDoubleFunction coefficientSupplier) {
        return getInstance(expansionPoint, coefficientSupplier, DEFAULT_HARD_BOUND);
    }

    public static SeriesFunction getInstance(double expansionPoint, IntToDoubleFunction coefficientSupplier, int hardBound) {
        return new SeriesFunction(expansionPoint, coefficientSupplier, hardBound);
    }

    public static SeriesFunction arctan() {
        return getInstance(0, num -> (num % 2 == 0 ? 0.0 : (num % 4 == 1 ? 1.0 : -1.0)) / (num == 0 ? 1 : num));
    }

    private final double expansionPoint;
    private final IntToDoubleFunction coefficientSupplier;
    private final int hardBound;

    /* Following launches of same function will be faster than the first */
    private final Double[] coefficientCache;

    private SeriesFunction(double expansionPoint, IntToDoubleFunction coefficientSupplier, int hardBound) {
        this.expansionPoint = expansionPoint;
        this.coefficientSupplier = coefficientSupplier;
        this.hardBound = hardBound;
        this.coefficientCache = new Double[this.hardBound];
    }

    public double get(double x, double precision) {
        double result = getCoefficient(0);
        double prev = result;
        double term = 1;

        for (int i = 1; i < this.hardBound; i++) {
            prev = result;
            double coefficient = getCoefficient(i);
            term *= x - this.expansionPoint;

            /*
             * Terms can be skipped - e.g., arctan(x) skips every even term
             * Precision checking relies on difference between two last sums - not skipping could mess it up
             */
            if (coefficient == 0.0) continue;

            result += coefficient * term;

            /* Infinite numbers always fail precision check - need a fast exit point for diverging series */
            if (Double.isInfinite(result)) return result;

            /* Could happen if coefficient supplier is invalid - zero-zero division, negative square root, etc. */
            if (Double.isNaN(result)) throw new ArithmeticException("Cannot find value for x = " + x);

            /* As long as i < SOFT_BOUND, try getting the most precise result possible */
            if (result == prev) return result;

            /* Average of two last sums tends to be more precise for alternating series - e.g., arctan(x) */
            if (i >= SOFT_BOUND && Math.abs(result - prev) < precision) return (result + prev) / 2;
        }

        return (result + prev) / 2;
    }

    public double get(double x) {
        return get(x, DEFAULT_PRECISION);
    }

    private double getCoefficient(int num) {
        if (this.coefficientCache[num] == null)
            this.coefficientCache[num] = this.coefficientSupplier.applyAsDouble(num);
        return this.coefficientCache[num];
    }
}
