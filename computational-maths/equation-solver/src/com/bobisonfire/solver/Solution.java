package com.bobisonfire.solver;

import com.bobisonfire.function.Function;

import java.util.Arrays;

public class Solution {
    private Function function;
    private double[] roots;

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public double[] getRoots() {
        return roots;
    }

    public void setRoots(double[] roots) {
        this.roots = roots;
    }

    public Solution(Function function, double[] roots) {
        this.function = function;
        this.roots = roots;
    }

    @Override
    public String toString() {
        if (roots.length == 0) return "No solutions";
        return Arrays.toString(roots);
    }
}
