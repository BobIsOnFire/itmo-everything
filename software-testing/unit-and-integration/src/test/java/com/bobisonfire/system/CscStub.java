package com.bobisonfire.system;

import static com.bobisonfire.Utils.arePrecise;
import static java.lang.Math.*;

public class CscStub implements Csc {
    private static final Point[] valueTable = {
            new Point(0,          Double.POSITIVE_INFINITY),
            new Point(PI / 6,     2.0),
            new Point(PI / 4,     sqrt(2)),
            new Point(PI / 3,     2 / sqrt(3)),
            new Point(PI / 2,     1),
            new Point(2 * PI / 3, 2 / sqrt(3)),
            new Point(3 * PI / 4, sqrt(2)),
            new Point(5 * PI / 6, 2.0)
    };

    @Override
    public double get(double x, double precision) {
        double cut = Math.abs(x % PI);
        int sign = 1;
        if (x >= 0 && x % (2 * PI) > PI) sign *= -1;
        if (x < 0 && x % (2 * PI) > -PI) sign *= -1;

        for (Point p : valueTable) {
            if (arePrecise(cut, p.getX(), precision)) return sign * p.getY();
        }

        throw new UnsupportedOperationException("Cannot find a value for " + x + " in value table");
    }
}
