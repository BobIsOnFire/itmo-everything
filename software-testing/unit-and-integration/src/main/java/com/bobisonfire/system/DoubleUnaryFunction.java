package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface DoubleUnaryFunction extends ModuleFunction {
    double get(double x, double precision);
    default double get(double x) {
        return get(x, SeriesFunction.DEFAULT_PRECISION);
    }

    default Point[] getValues(double begin, double end, double step) {
        List<Point> points = new ArrayList<>();
        double precision = SeriesFunction.DEFAULT_PRECISION;
        for (double x = begin; x <= end; x += step) {
            double result = get(x);
            if (Double.isFinite(result)) points.add(new Point(x, Math.round(result / precision) * precision));
            else points.add(new Point(x, result));
        }
        return points.toArray(new Point[]{});
    }

    default void saveValues(Path path, double begin, double end, double step) throws IOException {
        saveValues(path, getValues(begin, end, step));
    }
}
