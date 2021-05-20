package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.*;
import static org.junit.jupiter.api.Assertions.*;

public class CosTest {
    private static Cos cos;
    private final static double[] bases = {
            0, PI / 6, PI / 4, PI / 3, PI / 2, 2 * PI / 3, 3 * PI / 4, 5 * PI / 6
    };

    @BeforeAll
    static void setUp() {
        cos = new CosOperator();
    }

    private static Stream<Arguments> cos_positive_stream_gen() {
        return Stream.of(
                Arguments.of(-PI / 3, 0.5),
                Arguments.of(-PI / 4, 1 / sqrt(2)),
                Arguments.of(-PI / 6, sqrt(3) / 2),
                Arguments.of(0,       1),
                Arguments.of(PI / 6,  sqrt(3) / 2),
                Arguments.of(PI / 4,  1 / sqrt(2)),
                Arguments.of(PI / 3,  0.5),
                Arguments.of(PI / 2,  0)
        );
    }

    private static Stream<Arguments> cos_negative_stream_gen() {
        return Stream.of(
                Arguments.of(PI / 2,     0),
                Arguments.of(2 * PI / 3, -0.5),
                Arguments.of(3 * PI / 4, -1 / sqrt(2)),
                Arguments.of(5 * PI / 6, -sqrt(3) / 2),
                Arguments.of(PI,         -1),
                Arguments.of(7 * PI / 6, -sqrt(3) / 2),
                Arguments.of(5 * PI / 4, -1 / sqrt(2)),
                Arguments.of(4 * PI / 3, -0.5),
                Arguments.of(3 * PI / 2, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("cos_positive_stream_gen")
    public void test_positive_curved_up(double arg, double expected) {
        assertEquals(expected, cos.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("cos_negative_stream_gen")
    public void test_negative_curved_down(double arg, double expected) {
        assertEquals(expected, cos.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @Test
    public void test_periodic() {
        for (double base : bases) {
            assertEquals(cos.get(base),  cos.get(base + 2 * PI),  SeriesFunction.DEFAULT_PRECISION);
            assertEquals(cos.get(-base), cos.get(base - 2 * PI), SeriesFunction.DEFAULT_PRECISION);
        }
    }
}
