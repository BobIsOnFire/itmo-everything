package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.*;
import static java.lang.Math.E;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LnTest {
    private static Ln ln;

    @BeforeAll
    static void setUp() {
        ln = new LnOperator();
    }

    private static Stream<Arguments> ln_positive_stream_gen() {
        return Stream.of(
                Arguments.of(1,           0),
                Arguments.of(sqrt(E),     0.5),
                Arguments.of(2,           0.6931471806),
                Arguments.of(E,           1),
                Arguments.of(3,           1.0986122887),
                Arguments.of(5,           1.6094379124),
                Arguments.of(10,          2.3025850930),
                Arguments.of(20,          2.9957322735)
        );
    }

    private static Stream<Arguments> ln_negative_stream_gen() {
        return Stream.of(
                Arguments.of(0.1,         -2.3025850930),
                Arguments.of(1 / (E * E), -2),
                Arguments.of(0.2,         -1.6094379124),
                Arguments.of(1 / E,       -1),
                Arguments.of(0.5,         -0.6931471806),
                Arguments.of(0.77141145,  -0.2595334)
        );
    }

    @ParameterizedTest
    @MethodSource("ln_positive_stream_gen")
    public void test_positive(double arg, double expected) {
        assertEquals(expected, ln.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("ln_negative_stream_gen")
    public void test_negative(double arg, double expected) {
        assertEquals(expected, ln.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @Test
    public void test_infinite() {
        assertEquals(ln.get(0), Double.NEGATIVE_INFINITY, SeriesFunction.DEFAULT_PRECISION);
        assertEquals(ln.get(-1), Double.NEGATIVE_INFINITY, SeriesFunction.DEFAULT_PRECISION);
        assertEquals(ln.get(Double.POSITIVE_INFINITY), Double.POSITIVE_INFINITY, SeriesFunction.DEFAULT_PRECISION);
    }
}
