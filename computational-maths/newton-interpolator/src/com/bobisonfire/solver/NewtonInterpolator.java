package com.bobisonfire.solver;

import com.bobisonfire.function.Function;
import com.bobisonfire.function.Point;

public class NewtonInterpolator {
    public static Function getInterpolationPolynom(Point[] points, String x, String y) {
        int len = points.length;
        double[] arguments = new double[len];
        double[] constants = new double[len];
        double[] diagonal = new double[len];

        for (int i = 0; i < len; i++) {
            arguments[i] = points[i].get(x);
            constants[i] = points[i].get(y);
        }

        diagonal[0] = constants[len - 1];

        for (int i = 1; i < len; i++) {
            double[] temp = new double[len];
            for (int j = i; j < len; j++) {
                temp[j] = (constants[j] - constants[j - 1]) / (arguments[j] - arguments[j - i]);
            }
            System.arraycopy(temp, i, constants, i, len - i);
            diagonal[i] = constants[len - 1];
        }

        return new NewtonPolynom(arguments, constants, diagonal);
    }

    public static void addPoint(Function f, double x, double y) {
        if (!(f instanceof NewtonPolynom)) throw new SolverException("Operation is supported for Newton polynoms only.");

        NewtonPolynom p = (NewtonPolynom) f;
        int len = p.arguments.length;

        double[] arguments = new double[len + 1];
        System.arraycopy(p.arguments, 0, arguments, 0, len);
        double[] constants = new double[len + 1];
        System.arraycopy(p.constants, 0, constants, 0, len);
        double[] diagonal = new double[len + 1];

        arguments[len] = x;
        diagonal[0] = y;

        for (int i = 1; i < len + 1; i++) {
            diagonal[i] = (diagonal[i - 1] - p.diagonal[i - 1]) / (arguments[len] - arguments[len - i]);
        }

        constants[len] = diagonal[len];

        p.arguments = arguments;
        p.constants = constants;
        p.diagonal = diagonal;
    }
}
