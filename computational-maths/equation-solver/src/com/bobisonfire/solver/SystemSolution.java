package com.bobisonfire.solver;

import com.bobisonfire.function.Function;
import com.bobisonfire.function.Point;

import java.util.Arrays;

public class SystemSolution {
    private Function[] functions;
    private Point[] roots;
    private String[] variables;

    public Function[] getFunctions() {
        return functions;
    }

    public void setFunctions(Function[] functions) {
        this.functions = functions;
    }

    public String[] getVariables() {
        return variables;
    }

    public void setVariables(String[] variables) {
        this.variables = variables;
    }

    public Point[] getRoots() {
        return roots;
    }

    public void setRoots(Point[] roots) {
        this.roots = roots;
    }

    public SystemSolution(Function[] functions, Point[] roots, String[] variables) {
        this.functions = functions;
        this.roots = roots;
        this.variables = variables;
    }

    @Override
    public String toString() {
        if (roots.length == 0) return "No solutions";
        return Arrays.toString(roots);
    }
}
