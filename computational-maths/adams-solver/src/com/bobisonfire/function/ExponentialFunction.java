package com.bobisonfire.function;

public class ExponentialFunction implements ExtendedFunction {
    private final double base;
    private final ExtendedFunction power;

    public ExponentialFunction(double base, Function power) {
        this.base = base;
        this.power = (ExtendedFunction) power;
    }

    public static ExtendedFunction from(double base, Function power) {
        if (base <= 0 || base == 1) throw new FunctionException("Invalid exponent base: " + base);
        if (power instanceof Constant) return Constant.from(Math.pow(base, power.getValue(0)));

        return new ExponentialFunction(base, power);
    }

    @Override
    public double getValue(double x) {
        return Math.pow(base, power.getValue(x));
    }

    @Override
    public double getValue(Point p) {
        return Math.pow(base, power.getValue(p));
    }

    @Override
    public ExtendedFunction getDerivative(String variable) {
        return FunctionMul.from(
                Constant.from(Math.log(base)),
                this,
                power.getDerivative(variable)
        );
    }

    @Override
    public String toString() {
        String baseString = Constant.from(base).toString();

        if (power instanceof Variable) return baseString + "^" + power;
        return baseString + "^(" + power + ")";
    }

    @Override
    public boolean isIdentical(ExtendedFunction o) {
        if (!(o instanceof ExponentialFunction)) return false;
        ExponentialFunction exp = (ExponentialFunction) o;
        return base == exp.base && power.isIdentical(exp.power);
    }

    @Override
    public ExtendedFunction multiplyIfPossible(ExtendedFunction o) {
        if (o instanceof PowerFunction) return o.multiplyIfPossible(this);

        if (o instanceof ExponentialFunction) {
            ExponentialFunction exp = (ExponentialFunction) o;
            if (base == exp.base) return ExponentialFunction.from(base, FunctionSum.from(
                    power,
                    exp.power
            ));
            if (power.isIdentical(exp.power)) return ExponentialFunction.from(base * exp.base, power);
        }
        return null;
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
