package com.bobisonfire.matrix;

import static java.lang.Math.*;

public class Rational implements Comparable<Rational> {
    public static final Rational ZERO = from(0);
    public static final Rational ONE = from(1);

    private int nom;
    private int denom;

    public static Rational from(int nom, int denom) {
        if (denom == 0) throw new ArithmeticException();
        Rational x = new Rational();

        int factor = greatestFactor(nom, denom);
        int sign = denom > 0 ? 1 : -1;
        x.nom = sign * nom / factor;
        x.denom = sign * denom / factor;
        return x;
    }

    public static Rational from(int x) {
        return from(x, 1);
    }

    public static Rational from(Rational r) {
        return from(r.nom, r.denom);
    }

    private static int greatestFactor(int a, int b) {
        int x1 = max(abs(a), abs(b));
        int x2 = min(abs(a), abs(b));

        while (x2 != 0) {
            int r = x1 % x2;
            x1 = x2;
            x2 = r;
        }

        return x1;
    }

    private static int leastMultiple(int a, int b) {
        return (a * b) / greatestFactor(a, b);
    }

    public Rational add(Rational r) {
        int newDenom = leastMultiple(denom, r.denom);
        int newNom = nom * (newDenom / denom) + r.nom * (newDenom / r.denom);

        return from(newNom, newDenom);
    }

    public Rational add(int x) {
        return add(from(x));
    }

    public Rational multiply(Rational r) {
        return from(nom * r.nom, denom * r.denom);
    }

    public Rational multiply(int x) {
        return multiply(from(x));
    }

    public Rational subtract(Rational r) {
        return add(r.multiply(-1));
    }

    public Rational subtract(int x) {
        return add(-x);
    }

    public Rational divide(Rational r) {
        return from(nom * r.denom, denom * r.nom);
    }

    public Rational divide(int x) {
        return divide(from(1, x));
    }

    public Rational negate() {
        return from(-nom, denom);
    }

    public int toInt() {
        return nom / denom;
    }

    public double toDouble() {
        return (double) nom / denom;
    }

    @Override
    public int compareTo(Rational o) {
        return this.subtract(o).nom;
    }

    @Override
    public boolean equals(Object obj) {
        if ( !(obj instanceof Rational) ) return false;

        Rational x = (Rational) obj;
        return compareTo(x) == 0;
    }

    @Override
    public String toString() {
        if (denom == 1) return Integer.toString(nom);
        return nom + "/" + denom;
    }
}
