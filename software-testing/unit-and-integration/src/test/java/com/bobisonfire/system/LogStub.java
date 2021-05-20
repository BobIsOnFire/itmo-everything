package com.bobisonfire.system;

import static com.bobisonfire.Utils.arePrecise;
import static java.lang.Math.*;

public class LogStub implements Log {
    private static final Point[] lnValueTable = {
            new Point(0,           Double.NEGATIVE_INFINITY),
            new Point(0.1,         -2.3025850930),
            new Point(1 / (E * E), -2),
            new Point(0.2,         -1.6094379124),
            new Point(1 / E,       -1),
            new Point(0.5,         -0.6931471806),
            new Point(0.77141145,  -0.2595334),
            new Point(1,           0),
            new Point(sqrt(E),     0.5),
            new Point(2,           0.6931471806),
            new Point(E,           1),
            new Point(3,           1.0986122887),
            new Point(5,           1.6094379124),
            new Point(10,          2.3025850930),
            new Point(20,          2.9957322735),
    };

    private double lookup(double value, double precision) {
        for (Point p : lnValueTable) {
            if (arePrecise(value, p.getX(), precision)) return p.getY();
        }

        throw new UnsupportedOperationException("Cannot find a value for " + value + " in value table");
    }

    @Override
    public double get(double base, double x, double precision) {
        return lookup(x, precision) / lookup(base, precision);
    }
}
