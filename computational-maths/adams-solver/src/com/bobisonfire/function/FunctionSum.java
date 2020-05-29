package com.bobisonfire.function;

import java.util.*;

public class FunctionSum implements ExtendedFunction {
    private final ExtendedFunction[] functions;

    private FunctionSum(ExtendedFunction[] functions) {
        this.functions = functions;
    }

    public static ExtendedFunction from(Function... functions) {
        List<ExtendedFunction> allFunctionList = new ArrayList<>();
        for (Function f : functions) {
            ExtendedFunction ext = (ExtendedFunction) f;
            if (f == null) continue;
            if (!(f instanceof FunctionSum)) {
                allFunctionList.add(ext);
                continue;
            }
            allFunctionList.addAll(Arrays.asList(((FunctionSum) ext).functions));
        }

        LinkedList<ExtendedFunction> transformedList = new LinkedList<>();
        double constants = 0;
        for (ExtendedFunction f : allFunctionList) {
            if (f instanceof Constant) {
                constants += f.getValue(0);
                continue;
            }

            ListIterator<ExtendedFunction> iter = transformedList.listIterator();
            boolean added = false;
            while (iter.hasNext()) {
                ExtendedFunction sum = iter.next().addIfPossible(f);
                if (sum != null) {
                    iter.set(sum);
                    added = true;
                    break;
                }
            }

            if (!added) iter.add(f);
        }

        if (constants != 0) transformedList.addLast(Constant.from(constants));
        if (transformedList.size() == 0) return Constant.from(0);
        if (transformedList.size() == 1) return transformedList.getFirst();

        return new FunctionSum(transformedList.toArray( new ExtendedFunction[]{} ));
    }

    @Override
    public double getValue(double x) {
        double result = 0;
        for (Function f : functions) result += f.getValue(x);
        return result;
    }

    @Override
    public double getValue(Point p) {
        double result = 0;
        for (ExtendedFunction f : functions) result += f.getValue(p);
        return result;
    }

    @Override
    public ExtendedFunction getDerivative(String variable) {
        ExtendedFunction[] derivatives = new ExtendedFunction[functions.length];
        for (int i = 0; i < functions.length; i++) derivatives[i] = functions[i].getDerivative(variable);
        return FunctionSum.from(derivatives);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Function f : functions) {
            if (f instanceof Constant && f.getValue(0) < 0) {
                double abs = Math.abs(f.getValue(0));
                sb.append(" - ").append(Constant.from(abs).toString());
            }
            else if (f instanceof FunctionMul && ((FunctionMul) f).getConstant() < 0) {
                sb.append(f.toString());
            }
            else {
                if (!first) sb.append(" + ");
                sb.append(f.toString());
            }

            first = false;
        }

        return sb.toString();
    }

    @Override
    public boolean isIdentical(ExtendedFunction o) {
        if (!(o instanceof FunctionSum)) return false;

        FunctionSum f = (FunctionSum) o;
        if (functions.length != f.functions.length) return false;

        int len = functions.length;
        boolean[] alreadyUsed = new boolean[len];

        for (ExtendedFunction el : functions) {
            for (int j = 0; j < len; j++) {
                if (alreadyUsed[j]) continue;
                if (el.isIdentical(f.functions[j])) {
                    alreadyUsed[j] = true;
                    break;
                }
            }
        }

        for (int j = 0; j < len; j++) {
            if (!alreadyUsed[j]) return false;
        }

        return true;
    }

    @Override
    public ExtendedFunction multiplyIfPossible(ExtendedFunction o) {
        if (o instanceof PowerFunction) o.multiplyIfPossible(this);
        if (!isIdentical(o)) return null;

        return PowerFunction.from(this, 2);
    }

    @Override
    public ExtendedFunction addIfPossible(ExtendedFunction o) {
        return null;
    }
}
