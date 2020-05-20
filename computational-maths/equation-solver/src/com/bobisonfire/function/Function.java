package com.bobisonfire.function;

public interface Function {
    double getValue(double x);
    double getValue(Point p);

    Function getDerivative(String variable);

    boolean isIdentical(Function o);
    Function multiplyIfPossible(Function o);
    Function addIfPossible(Function o);
}
