package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface DoubleBinaryFunction extends ModuleFunction {
    double get(double base, double x, double precision);
    default double get(double base, double x) {
        return get(base, x, SeriesFunction.DEFAULT_PRECISION);
    }

    default Point[] getValues(double base, double begin, double end, double step) {
        List<Point> points = new ArrayList<>();
        double precision = SeriesFunction.DEFAULT_PRECISION;
        for (double x = begin; x <= end; x += step) {
            double result = get(base, x);
            if (Double.isFinite(result)) points.add(new Point(x, Math.round(result / precision) * precision));
            else points.add(new Point(x, result));

        }
        return points.toArray(new Point[]{});
    }

    default void saveValues(Path path, double base, double begin, double end, double step) throws IOException {
        saveValues(path, getValues(base, begin, end, step));
    }
}
