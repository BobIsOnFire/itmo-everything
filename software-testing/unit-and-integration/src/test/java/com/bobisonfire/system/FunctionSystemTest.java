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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionSystemTest {
    private static FunctionSystem allStubs;
    private static FunctionSystem allReal;

    @BeforeAll
    static void setUp() {
        Cos cosReal = new CosOperator();
        Cos cosStub = new CosStub();
        Ln lnReal = new LnOperator();
        Ln lnStub = new LnStub();

        Csc cscReal = new CscOperator(cosReal);
        Csc cscStub = new CscStub();

        Log logReal = new LogOperator(lnReal);
        Log logStub = new LogStub();

        allStubs = new FunctionSystemOperator(cosStub, cscStub, lnStub, logStub);
        allReal = new FunctionSystemOperator(cosReal, cscReal, lnReal, logReal);

    }

    private static Stream<Arguments> positive_stream_gen() {
        return Stream.of(
                Arguments.of(0.1,         11.0718470),
                Arguments.of(1 / (E * E), 8.1063886),
                Arguments.of(0.2,         4.9799553),
                Arguments.of(1 / E,       1.6800512),
                Arguments.of(0.5,         0.7468476),
                Arguments.of(0.77141145,  0.2598188),
                Arguments.of(sqrt(E),     1.7541339),
                Arguments.of(2,           2.6107671),
                Arguments.of(E,           4.3691186),
                Arguments.of(3,           5.0377640),
                Arguments.of(5,           9.3078423),
                Arguments.of(10,          17.263654),
                Arguments.of(20,          27.708490)
        );
    }

    private static Stream<Arguments> negative_stream_gen() {
        return Stream.of(
                Arguments.of(-PI / 6,      8.75),
                Arguments.of(-PI / 4,      0.5 + 2 * sqrt(2)),
                Arguments.of(-PI / 3,      0.25 + 8.0/9 * sqrt(3)),
                Arguments.of(-PI / 2,      1),
                Arguments.of(-2 * PI / 3,  0.25 + 8.0/9 * sqrt(3)),
                Arguments.of(-3 * PI / 4,  0.5 + 2 * sqrt(2)),
                Arguments.of(-5 * PI / 6,  8.75),
                Arguments.of(-7 * PI / 6,  -7.25),
                Arguments.of(-5 * PI / 4,  0.5 - 2 * sqrt(2)),
                Arguments.of(-4 * PI / 3,  0.25 - 8.0/9 * sqrt(3)),
                Arguments.of(-3 * PI / 2,  -1),
                Arguments.of(-5 * PI / 3,  0.25 - 8.0/9 * sqrt(3)),
                Arguments.of(-7 * PI / 4,  0.5 - 2 * sqrt(2)),
                Arguments.of(-11 * PI / 6, -7.25)
        );
    }

    @ParameterizedTest
    @MethodSource("negative_stream_gen")
    public void test_trigonometric(double arg, double expected) {
        assertEquals(expected, allStubs.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("positive_stream_gen")
    public void test_logarithmic(double arg, double expected) {
        assertEquals(expected, allStubs.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("negative_stream_gen")
    public void test_trigonometric_real(double arg, double expected) {
        assertEquals(expected, allReal.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("positive_stream_gen")
    public void test_logarithmic_real(double arg, double expected) {
        assertEquals(expected, allReal.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }


    @Test
    public void test_infinite() {
        assertTrue(Double.isInfinite(allStubs.get(0)));
        assertTrue(Double.isInfinite(allStubs.get(-PI)));
        assertTrue(Double.isInfinite(allStubs.get(-2 * PI)));
    }

    @Test
    public void test_nan() {
        assertTrue(Double.isNaN(allStubs.get(1)));
    }

    @Test
    public void test_infinite_real() {
        assertTrue(Double.isInfinite(allReal.get(0)));
        assertTrue(Double.isInfinite(allReal.get(-PI)));
        assertTrue(Double.isInfinite(allReal.get(-2 * PI)));
    }

    @Test
    public void test_nan_real() {
        assertTrue(Double.isNaN(allStubs.get(1)));
    }
}
