package com.bobisonfire.function;

public class TrigonometricFunction implements ExtendedFunction {
    private final ExtendedFunction argument;
    private final boolean isSin;

    private TrigonometricFunction(ExtendedFunction argument, boolean isSin) {
        this.isSin = isSin;
        this.argument = argument;
    }

    public static ExtendedFunction from(Function argument, boolean isSin) {
        ExtendedFunction extArgument = (ExtendedFunction) argument;
        if (extArgument instanceof Constant) {
            double value = extArgument.getValue(0);
            return Constant.from(isSin ? Math.sin(value) : Math.cos(value));
        }

        return new TrigonometricFunction(extArgument, isSin);
    }

    @Override
    public double getValue(double x) {
        double value = argument.getValue(x);
        return isSin ? Math.sin(value) : Math.cos(value);
    }

    @Override
    public double getValue(Point p) {
        double value = argument.getValue(p);
        return isSin ? Math.sin(value) : Math.cos(value);
    }

    @Override
    public ExtendedFunction getDerivative(String variable) {
        return FunctionMul.from(
                Constant.from(isSin ? 1 : -1),
                from(argument, !isSin),
                argument.getDerivative(variable)
        );
    }

    @Override
    public String toString() {
        String function = isSin ? "sin" : "cos";
        return function + "(" + argument + ")";
    }

    @Override
    public boolean isIdentical(ExtendedFunction o) {
        if (!(o instanceof TrigonometricFunction)) return false;
        TrigonometricFunction t = (TrigonometricFunction) o;
        return argument.isIdentical(t.argument) && isSin == t.isSin;
    }

    @Override
    public ExtendedFunction multiplyIfPossible(ExtendedFunction o) {
        if (o instanceof PowerFunction) return o.multiplyIfPossible(this);
        if (!isIdentical(o)) return null;
        return PowerFunction.from(this, 2);
    }

    @Override
    public ExtendedFunction addIfPossible(ExtendedFunction o) {
        if (o instanceof FunctionMul) return o.addIfPossible(this);
        if (!isIdentical(o)) return null;
        return FunctionMul.from(
                Constant.from(2),
                this
        );
    }
}
