package com.bobisonfire.solver;

import com.bobisonfire.function.ExtendedFunction;
import com.bobisonfire.function.Function;
import com.bobisonfire.function.Point;

import static java.lang.Math.*;

public class AdamsSolver {
    private final ExtendedFunction function;
    private final String varX;
    private final String varY;

    public AdamsSolver(ExtendedFunction function, String varX, String varY) {
        this.function = function;
        this.varX = varX;
        this.varY = varY;
    }

    public AdamsSolver(Function function, String x, String y) {
        this((ExtendedFunction) function, x, y);
    }

    public Point[] solve(Point start, double end, double precision) {
        double step = pow(precision, 0.25);
        int len = (int) ((end - start.get(varX)) / step) + 1;

        Point[] result = new Point[len];
        result[0] = start.copy();

        double[] right = new double[len];
        right[0] = function.getValue(result[0]);

        for (int i = 1; i < min(len, 4); i++) {
            Point p = result[i - 1].copy();
            double x = p.get(varX);
            double y = p.get(varY);

            double k0 = right[i - 1];

            p.put(varX, x + step / 2);
            p.put(varY, y + k0 / 2);
            double k1 = function.getValue(p);

            p.put(varY, y + k1 / 2);
            double k2 = function.getValue(p);

            p.put(varX, x + step);
            p.put(varY, y + k2);
            double k3 = function.getValue(p);

            p.put(varY, y + step / 6 * (k0 + 2 * k1 + 2 * k2 + k3));
            result[i] = p;
            right[i] = function.getValue(p);
        }

        if (len <= 4) return result;

        double[] multipliers = {
                step,
                pow(step, 2) / 2,
                5 * pow(step, 3) / 12,
                3 * pow(step, 4) / 8
        };

        for (int i = 4; i < len; i++) {
            result[i] = new Point();

            double[] delta = {
                    right[i - 1],
                    right[i - 1] - right[i - 2],
                    right[i - 1] - 2 * right[i - 2] + right[i - 3],
                    right[i - 1] - 3 * right[i - 2] + 3 * right[i - 3] - right[i - 4]
            };

            double y = result[i - 1].get(varY);
            for (int j = 0; j < 4; j++) y += multipliers[j] * delta[j];

            result[i].put(varX, result[i - 1].get(varX) + step);
            result[i].put(varY, y);

            right[i] = function.getValue(result[i]);
        }

        return result;
    }
}
