package com.bobisonfire.function;

public class PowerFunction implements ExtendedFunction {
    private final ExtendedFunction base;
    private final int power;

    private PowerFunction(ExtendedFunction base, int power) {
        this.base = base;
        this.power = power;
    }

    public static ExtendedFunction from(Function base, int power) {
        ExtendedFunction extBase = (ExtendedFunction) base;
        if (power < 0) throw new FunctionException("Invalid power value: " + power);
        if (power == 0) return Constant.from(1);
        if (power == 1) return extBase;

        if (base instanceof Constant) return Constant.from( Math.pow(extBase.getValue(0), power) );

        if (base instanceof PowerFunction) {
            PowerFunction p = (PowerFunction) extBase;
            return from(p.base, p.power * power);
        }

        return new PowerFunction(extBase, power);
    }

    @Override
    public double getValue(double x) {
        return Math.pow(base.getValue(x), power);
    }

    @Override
    public double getValue(Point p) {
        return Math.pow(base.getValue(p), power);
    }

    @Override
    public ExtendedFunction getDerivative(String variable) {
        return FunctionMul.from(
                Constant.from(power),
                PowerFunction.from(base, power - 1),
                base.getDerivative(variable)
        );
    }

    @Override
    public String toString() {
        if (base instanceof Variable) return base + "^" + power;
        return "(" + base + ")^" + power;
    }

    @Override
    public boolean isIdentical(ExtendedFunction o) {
        if (!(o instanceof PowerFunction)) return false;
        PowerFunction p = (PowerFunction) o;
        return base.isIdentical(p.base) && power == p.power;
    }

    @Override
    public ExtendedFunction multiplyIfPossible(ExtendedFunction o) {
        if (o instanceof PowerFunction) {
            PowerFunction p = (PowerFunction) o;
            if (base.isIdentical(p.base)) return PowerFunction.from(base, power + p.power);
        }

        if (base.isIdentical(o)) return PowerFunction.from(base, power + 1);
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
