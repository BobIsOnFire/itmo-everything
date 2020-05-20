package com.bobisonfire.solver;

import com.bobisonfire.function.*;

import java.util.ArrayList;
import java.util.List;

public class NewtonSystemSolver {
    private static final int MAX_ITERATIONS = 1000;

    private final Function[] functions;
    private final String[] variables;
    private final Function jacobian;
    private final Function[][] derivatives;

    public NewtonSystemSolver(Function... funcs) {
        int len = funcs.length;
        if (len != 2) throw new SolverException("I can't handle this sry");

        variables = Variable.variableNames();
        if (variables.length != len) throw new SolverException("Equation and variable amounts do not match.");
        Variable.clearPool();

        functions = new Function[len];
        System.arraycopy(funcs, 0, functions, 0, len);

        derivatives = new Function[len][len];
        for (int i = 0; i < len; i++) {
            for (int j = 0; j < len; j++) derivatives[i][j] = functions[i].getDerivative(variables[j]);
        }

        jacobian = FunctionSum.from(
                FunctionMul.from(derivatives[0][0], derivatives[1][1]),
                FunctionMul.from(Constant.from(-1), derivatives[0][1], derivatives[1][0])
        );
    }

    public SystemSolution getSolution(double a, double b, double precision, double stepSize) {
        if (precision <= 0) throw new SolverException("Precision value is not valid: " + precision);
        double x1 = Math.min(a, b);
        double x2 = Math.max(a, b);

        Point p = new Point();
        for (String v : variables) p.put(v, x1);

        List<Point> roots = new ArrayList<>();
        Point prevRoot = new Point();
        for (String v : variables) prevRoot.put(v, x1 - x2);

        do {
            Point next = p.copy();
            next.incEach(stepSize);

            if (hasRootBetween(p, next)) {
                Point root = findRoot(p, precision);
                if (root != null && !rootAlreadyFound(roots, root, precision)) {
                    prevRoot = root.copy();
                    roots.add(prevRoot);
                }
            }

            p = next(p, x1, x2, stepSize);
        } while (p != null);

        Point[] array = new Point[roots.size()];
        int k = 0;
        for (Point r : roots) array[k++] = r;

        return new SystemSolution(functions, array, variables);
    }

    private Point next(Point p, double x1, double x2, double stepSize) {
        for (String v : variables) {
            double value = p.get(v);
            if (value <= x2 - stepSize) {
                p.put(v, value + stepSize);
                return p;
            } else {
                p.put(v, x1);
            }
        }

        return null;
    }

    private boolean hasRootBetween(Point p1, Point p2) {
        for (Function f : functions) {
            if (f.getValue(p1) * f.getValue(p2) > 0) return false;
        }
        return true;
    }

    private boolean rootAlreadyFound(List<Point> roots, Point root, double precision) {
        for (Point p : roots) {
            if (p.isPrecise(root, precision)) return true;
        }
        return false;
    }

    // too sad this implementation is only for 2 variables
    private Point findRoot(Point p, double precision) {
        if (functions[0].getValue(p) == 0 && functions[1].getValue(p) == 0) return p;

        String var1 = variables[0];
        String var2 = variables[1];

        Point point = p.copy();
        Point newPoint = new Point();

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            if (jacobian.getValue(point) == 0) return null;

            double a = functions[0].getValue(point) / jacobian.getValue(point);
            double b = functions[1].getValue(point) / jacobian.getValue(point);

            double x = point.get(var1) - a * derivatives[1][1].getValue(point) + b * derivatives[0][1].getValue(point);
            double y = point.get(var2) + a * derivatives[1][0].getValue(point) - b * derivatives[0][0].getValue(point);

            newPoint.put(var1, x);
            newPoint.put(var2, y);

            if (point.isPrecise(newPoint, precision)) return newPoint;

            point.put(var1, x);
            point.put(var2, y);
        }

        return newPoint;
    }
}
