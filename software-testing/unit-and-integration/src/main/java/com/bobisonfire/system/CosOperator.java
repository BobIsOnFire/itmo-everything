package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.LongStream;

import static java.lang.Math.PI;
import static java.lang.Math.abs;

public class CosOperator implements Cos {
    private static final AtomicInteger size = new AtomicInteger(1);
    private static final long[] factorials = LongStream
            .iterate(1, n -> n * size.getAndIncrement())
            .limit(20)
            .toArray();

    // cos(x) = 1 - x^2 / 2! + x^4 / 4! - ...
    // converges for all x
    private static final SeriesFunction COS_SERIES = SeriesFunction.getInstance(0,
            num -> (num % 2 == 1 ? 0.0 : ( num % 4 == 0 ? 1.0 : -1.0 )) / factorials[num], size.get());

    @Override
    public double get(double x, double precision) {
        double arg = abs(x % (2 * PI));
        double value = COS_SERIES.get(arg % PI, precision);
        if (arg >= PI) value *= -1;
        return value;
    }
}
