package com.bobisonfire.system;

import com.bobisonfire.SeriesFunction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static java.lang.Math.E;
import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogTest {
    private static Log logStubbed;
    private static Log logReal;

    @BeforeAll
    static void setUp() {
        logStubbed = new LogOperator(new LnStub());
        logReal = new LogOperator(new LnOperator());
    }

    private static Stream<Arguments> ln_stream_gen() {
        return Stream.of(
                Arguments.of(0.1,         -2.3025850930),
                Arguments.of(1 / (E * E), -2),
                Arguments.of(0.2,         -1.6094379124),
                Arguments.of(1 / E,       -1),
                Arguments.of(0.5,         -0.6931471806),
                Arguments.of(0.77141145,  -0.2595334),
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

    @ParameterizedTest
    @MethodSource("ln_stream_gen")
    public void test_log3(double arg, double expected) {
        assertEquals(expected / 1.098612, logStubbed.get(3, arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("ln_stream_gen")
    public void test_log5(double arg, double expected) {
        assertEquals(expected / 1.609437, logStubbed.get(5, arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("ln_stream_gen")
    public void test_log10(double arg, double expected) {
        assertEquals(expected / 2.302585, logStubbed.get(10, arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("ln_stream_gen")
    public void test_log3_real(double arg, double expected) {
        assertEquals(expected / 1.098612, logReal.get(3, arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("ln_stream_gen")
    public void test_log5_real(double arg, double expected) {
        assertEquals(expected / 1.609437, logReal.get(5, arg), SeriesFunction.DEFAULT_PRECISION);
    }

    @ParameterizedTest
    @MethodSource("ln_stream_gen")
    public void test_log10_real(double arg, double expected) {
        assertEquals(expected / 2.302585, logReal.get(10, arg), SeriesFunction.DEFAULT_PRECISION);
    }

}
