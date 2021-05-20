package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CscTest {
    private static Csc cscReal;
    private static Csc cscStubbed;
    private final static double[] bases = {
            0, PI / 6, PI / 4, PI / 3, PI / 2, 2 * PI / 3, 3 * PI / 4, 5 * PI / 6
    };

    @BeforeAll
    static void setUp() {
        cscReal = new CscOperator(new CosOperator());
        cscStubbed = new CscOperator(new CosStub());
    }

    private static Stream<Arguments> csc_positive_stream_gen() {
        return Stream.of(
                Arguments.of(PI / 6,     2.0),
                Arguments.of(PI / 4,     sqrt(2)),
                Arguments.of(PI / 3,     2 / sqrt(3)),
                Arguments.of(PI / 2,     1),
                Arguments.of(2 * PI / 3, 2 / sqrt(3)),
                Arguments.of(3 * PI / 4, sqrt(2)),
                Arguments.of(5 * PI / 6, 2.0)
        );
    }

    private static Stream<Arguments> csc_negative_stream_gen() {
        return Stream.of(
                Arguments.of(-PI / 6,     -2.0),
                Arguments.of(-PI / 4,     -sqrt(2)),
                Arguments.of(-PI / 3,     -2 / sqrt(3)),
                Arguments.of(-PI / 2,     -1),
                Arguments.of(-2 * PI / 3, -2 / sqrt(3)),
                Arguments.of(-3 * PI / 4, -sqrt(2)),
                Arguments.of(-5 * PI / 6, -2.0)
        );
    }

    @Test
    public void test_infinite() {
        assertTrue(Double.isInfinite(cscStubbed.get(0)));
        assertTrue(Double.isInfinite(cscStubbed.get(PI)));
        assertTrue(Double.isInfinite(cscStubbed.get(-PI)));
    }

    @Test
    public void test_infinite_real() {
        assertTrue(Double.isInfinite(cscReal.get(0)));
        assertTrue(Double.isInfinite(cscReal.get(PI)));
        assertTrue(Double.isInfinite(cscReal.get(-PI)));
    }


    @ParameterizedTest
    @MethodSource("csc_positive_stream_gen")
    public void test_positive_curved_down(double arg, double expected) {
        assertEquals(expected, cscStubbed.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("csc_positive_stream_gen")
    public void test_positive_curved_down_real(double arg, double expected) {
        assertEquals(expected, cscReal.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }


    @ParameterizedTest
    @MethodSource("csc_negative_stream_gen")
    public void test_negative_curved_up(double arg, double expected) {
        assertEquals(expected, cscStubbed.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("csc_negative_stream_gen")
    public void test_negative_curved_up_real(double arg, double expected) {
        assertEquals(expected, cscReal.get(arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @Test
    public void test_periodic() {
        for (double base : bases) {
            assertEquals(cscStubbed.get(base),  cscStubbed.get(base + 2 * PI),  SeriesFunction.DEFAULT_PRECISION);
            assertEquals(cscStubbed.get(-base), cscStubbed.get(-base - 2 * PI), SeriesFunction.DEFAULT_PRECISION);
        }
    }

    @Test
    public void test_periodic_real() {
        for (double base : bases) {
            assertEquals(cscReal.get(base),  cscReal.get(base + 2 * PI),  SeriesFunction.DEFAULT_PRECISION);
            assertEquals(cscReal.get(-base), cscReal.get(-base - 2 * PI), SeriesFunction.DEFAULT_PRECISION);
        }
    }
}
