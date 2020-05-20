package com.bobisonfire.function;

import java.util.Map;
import java.util.TreeMap;

public class Variable implements Function {
    private static final Map<String, Variable> variablePool = new TreeMap<>();
    private final String name;

    private Variable(String name) {
        this.name = name;
    }

    public static Function from(String name) {
        if (name.equalsIgnoreCase("e")) return Constant.from(Math.E);
        if (name.equalsIgnoreCase("p") || name.equalsIgnoreCase("п")) return Constant.from(Math.PI);

        if (variablePool.containsKey(name)) return variablePool.get(name);

        Variable v = new Variable(name);
        variablePool.put(name, v);
        return v;
    }

    @Override
    public double getValue(double x) {
        return x;
    }

    @Override
    public double getValue(Point p) {
        return p.get(name);
    }

    @Override
    public Function getDerivative(String variable) {
        if (variable.equals(name)) return Constant.from(1);
        return Constant.from(0);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean isIdentical(Function o) {
        if (!(o instanceof Variable)) return false;
        return name.equals(((Variable) o).name);
    }

    @Override
    public Function multiplyIfPossible(Function o) {
        if (o instanceof PowerFunction) return o.multiplyIfPossible(this);
        if (!isIdentical(o)) return null;
        return PowerFunction.from(this, 2);
    }

    @Override
    public Function addIfPossible(Function o) {
        if (o instanceof FunctionMul) return o.addIfPossible(this);
        if (!isIdentical(o)) return null;
        return FunctionMul.from(
                Constant.from(2),
                this
        );
    }

    public static void clearPool() {
        variablePool.clear();
    }

    public static String[] variableNames() {
        return variablePool.keySet().toArray(new String[0]);
    }
}
