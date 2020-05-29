package com.bobisonfire.function;

public class Constant implements ExtendedFunction {
    private final double value;

    private Constant(double value) {
        this.value = value;
    }

    public static ExtendedFunction from(double value) {
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
    public ExtendedFunction getDerivative(String variable) {
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
    public boolean isIdentical(ExtendedFunction o) {
        if (!(o instanceof Constant)) return false;
        return value == ((Constant) o).value;
    }

    @Override
    public ExtendedFunction multiplyIfPossible(ExtendedFunction o) {
        if (!(o instanceof Constant)) return null;
        return Constant.from(value * ((Constant) o).value);
    }

    @Override
    public ExtendedFunction addIfPossible(ExtendedFunction o) {
        if (!(o instanceof Constant)) return null;
        return Constant.from(value + ((Constant) o).value);
    }
}
