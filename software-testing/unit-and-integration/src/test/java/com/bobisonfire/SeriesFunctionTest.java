package com.bobisonfire;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static java.lang.Math.*;

class SeriesFunctionTest {
    private static SeriesFunction arctan;

    @BeforeAll
    static void setUp() {
        arctan = SeriesFunction.arctan();
    }

    private static Stream<Arguments> arctan_positive_stream_gen() {
        return Stream.of(
                Arguments.of(2 - sqrt(3), PI / 12),
                Arguments.of(sqrt(2) - 1, PI / 8),
                Arguments.of(1 / sqrt(3), PI / 6),
                Arguments.of(          1, PI / 4)
        );
    }

    private static Stream<Arguments> arctan_negative_stream_gen() {
        return Stream.of(
                Arguments.of(          -1, -PI / 4),
                Arguments.of(-1 / sqrt(3), -PI / 6),
                Arguments.of( 1 - sqrt(2), -PI / 8),
                Arguments.of( sqrt(3) - 2, -PI / 12)
        );
    }

    @Test
    public void arctan_test_diverging_positive() {
        assertTrue( Double.isInfinite(arctan.get(2)) );
    }

    @Test
    public void arctan_test_diverging_negative() {
        assertTrue( Double.isInfinite(arctan.get(-2)) );
    }

    @Test
    public void arctan_test_zero() {
        assertEquals(0.0, arctan.get(0.0), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("arctan_positive_stream_gen")
    public void arctan_test_positive_curved_up(double arg, double expected) {
        assertEquals(expected, arctan.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("arctan_negative_stream_gen")
    public void arctan_test_negative_curved_down(double arg, double expected) {
        assertEquals(expected, arctan.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }
}
