package com.bobisonfire.function;

import java.util.*;

public class FunctionMul implements Function {
    private Function[] functions;

    private FunctionMul(Function[] functions) {
        this.functions = functions;
    }

    public static Function from(Function... functions) {
        for (Function f : functions)
            if (f != null && f.isIdentical(Constant.from(0))) return Constant.from(0);

        List<Function> allFunctionList = new ArrayList<>();
        for (Function f : functions) {
            if (f == null) continue;
            if (!(f instanceof FunctionMul)) {
                allFunctionList.add(f);
                continue;
            }
            allFunctionList.addAll(Arrays.asList(((FunctionMul) f).functions));
        }

        LinkedList<Function> transformedList = new LinkedList<>();
        double constants = 1;
        for (Function f : allFunctionList) {
            if (f instanceof Constant) {
                constants *= f.getValue(0);
                continue;
            }

            ListIterator<Function> iter = transformedList.listIterator();
            boolean added = false;
            while (iter.hasNext()) {
                Function mul = iter.next().multiplyIfPossible(f);
                if (mul != null) {
                    iter.set(mul);
                    added = true;
                    break;
                }
            }

            if (!added) iter.add(f);
        }

        if (constants != 1 || transformedList.size() == 0) transformedList.addFirst(Constant.from(constants));

        if (transformedList.size() == 0) return Constant.from(0);
        if (transformedList.size() == 1) return transformedList.getFirst();

        return new FunctionMul(transformedList.toArray( new Function[]{} ));
    }

    @Override
    public double getValue(double x) {
        double result = 1;
        for (Function f : functions) result *= f.getValue(x);
        return result;
    }

    @Override
    public double getValue(Point p) {
        double result = 1;
        for (Function f : functions) result *= f.getValue(p);
        return result;
    }

    @Override
    public Function getDerivative(String variable) {
        int start = 0;
        if (functions[0] instanceof Constant) start++;

        int end = functions.length;
        Function[] derivatives = new Function[end - start];
        int k = 0;

        for (int i = start; i < end; i++) {
            Function[] copy = Arrays.copyOf(functions, end);
            copy[i] = copy[i].getDerivative(variable);
            derivatives[k++] = FunctionMul.from(copy);
        }
        return FunctionSum.from(derivatives);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Function f : functions) {
            if (f instanceof Constant) {
                if (f.getValue(0) < 0) sb.append(" - ");
                double abs = Math.abs(f.getValue(0));
                if (abs != 1) sb.append(Constant.from(abs).toString());
                continue;
            }
            if (f instanceof ExponentialFunction || f instanceof FunctionSum || f instanceof PowerFunction) {
                sb.append("(").append(f.toString()).append(")");
                continue;
            }
            sb.append(f.toString());
        }
        return sb.toString();
    }

    public double getConstant() {
        if (functions[0] instanceof Constant) return functions[0].getValue(0);
        return 1;
    }

    @Override
    public boolean isIdentical(Function o) {
        return isApplicable(o);
    }

    private boolean isApplicable(Function o) {
        int start = 0;
        if (functions[0] instanceof Constant) start++;
        int end = functions.length;

        if (!(o instanceof FunctionMul)) {
            return end - start == 1 && functions[start].isIdentical(o);
        }

        FunctionMul f = (FunctionMul) o;
        int otherStart = 0;
        if (f.functions[0] instanceof Constant) otherStart++;
        int otherEnd = f.functions.length;

        if (end - start != otherEnd - otherStart) return false;
        boolean[] alreadyUsed = new boolean[end];

        for (int i = start; i < end; i++) {
            for (int j = otherStart; j < otherEnd; j++) {
                if (alreadyUsed[j]) continue;
                if (functions[i].isIdentical(f.functions[j])) {
                    alreadyUsed[j] = true;
                    break;
                }
            }
        }

        for (int j = otherStart; j < otherEnd; j++) {
            if (!alreadyUsed[j]) return false;
        }

        return true;
    }

    @Override
    public Function multiplyIfPossible(Function o) {
        return null;
    }

    @Override
    public Function addIfPossible(Function o) {
        if (!isApplicable(o)) return null;

        if (!(o instanceof FunctionMul)) {
            Function[] copy = Arrays.copyOf(functions, functions.length);
            copy[0] = Constant.from(functions[0].getValue(0) + 1);
            return FunctionMul.from(copy);
        }

        FunctionMul f = (FunctionMul) o;
        double value = 2;
        if (functions[0] instanceof Constant) value += functions[0].getValue(0) - 1;
        if (f.functions[0] instanceof Constant) value += f.functions[0].getValue(0) - 1;

        if (functions[0] instanceof Constant) {
            Function[] copy = Arrays.copyOf(functions, functions.length);
            copy[0] = Constant.from(value);
            return FunctionMul.from(copy);
        }

        return FunctionMul.from(Constant.from(value), this);
    }
}
