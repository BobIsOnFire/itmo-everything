package com.bobisonfire.function;

interface ExtendedFunction extends Function {
    double getValue(Point p);

    ExtendedFunction getDerivative(String variable);

    boolean isIdentical(ExtendedFunction o);
    ExtendedFunction multiplyIfPossible(ExtendedFunction o);
    ExtendedFunction addIfPossible(ExtendedFunction o);

}
