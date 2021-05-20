package com.bobisonfire.system;

import static com.bobisonfire.Utils.arePrecise;
import static java.lang.Math.*;

public class CosStub implements Cos {
    private static final Point[] valueTable = {
            new Point(0,          1),
            new Point(PI / 6,     sqrt(3) / 2),
            new Point(PI / 4,     1 / sqrt(2)),
            new Point(PI / 3,     0.5),
            new Point(PI / 2,     0),
            new Point(2 * PI / 3, -0.5),
            new Point(3 * PI / 4, -1 / sqrt(2)),
            new Point(5 * PI / 6, -sqrt(3) / 2)
    };

    @Override
    public double get(double x, double precision) {
        int sign = x >= 0 ? 1 : -1;
        double cut = Math.abs(x % PI);
        sign *= x % (2 * PI) <= PI ? 1 : -1;

        for (Point p : valueTable) {
            if (arePrecise(cut, p.getX(), precision)) return sign * p.getY();
        }

        throw new UnsupportedOperationException("Cannot find a value for " + x + " in value table");
    }
}
