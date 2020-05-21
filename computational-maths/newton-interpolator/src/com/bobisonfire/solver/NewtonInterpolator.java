package com.bobisonfire.solver;

import com.bobisonfire.function.Function;
import com.bobisonfire.function.Point;

public class NewtonInterpolator {
    public static Function getInterpolationPolynom(Point[] points, String x, String y) {
        int len = points.length;
        double[] arguments = new double[len];
        double[] constants = new double[len];

        for (int i = 0; i < len; i++) {
            arguments[i] = points[i].get(x);
            constants[i] = points[i].get(y);
        }

        for (int i = 1; i < len; i++) {
            double[] temp = new double[len];
            for (int j = i; j < len; j++) {
                temp[j] = (constants[j] - constants[j - 1]) / (arguments[j] - arguments[j - i]);
            }
            System.arraycopy(temp, i, constants, i, len - i);
        }

        return NewtonPolynom.from(arguments, constants);
    }
}
