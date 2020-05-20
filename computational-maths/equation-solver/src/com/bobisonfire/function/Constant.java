package com.bobisonfire.function;

public class Constant implements Function {
    private final double value;

    private Constant(double value) {
        this.value = value;
    }

    public static Function from(double value) {
        return new Constant(value);
    }

    @Override
    public double getValue(double x) {
        return value;
    }

    @Override
    public double getValue(Point p) {
        return value;
    }

    @Override
    public Function getDerivative(String variable) {
        return Constant.from(0);
    }

    @Override
    public String toString() {
        if (value == Math.PI) return "п";
        if (value == Math.E) return "e";
        if (Math.round(value) == value) return Integer.toString((int) value);
        return Double.toString(Math.round(value * 1E5) / 1E5);
    }

    @Override
    public boolean isIdentical(Function o) {
        if (!(o instanceof Constant)) return false;
        return value == ((Constant) o).value;
    }

    @Override
    public Function multiplyIfPossible(Function o) {
        if (!(o instanceof Constant)) return null;
        return Constant.from(value * ((Constant) o).value);
    }

    @Override
    public Function addIfPossible(Function o) {
        if (!(o instanceof Constant)) return null;
        return Constant.from(value + ((Constant) o).value);
    }
}
