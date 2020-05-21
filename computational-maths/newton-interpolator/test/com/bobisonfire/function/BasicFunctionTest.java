package com.bobisonfire.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicFunctionTest {
    private final Function x = Variable.from("x");

    @Test
    public void test_simple_sum() {
        Function f1 = FunctionSum.from(Constant.from(2), x);
        assertEquals(f1.toString(), "x + 2");
    }

    @Test
    public void test_simple_power() {
        Function f2 = PowerFunction.from(x, 2);
        assertEquals(f2.toString(), "x^2");
    }

    @Test
    public void test_exponent_with_mul() {
        Function f3 = ExponentialFunction.from(2, FunctionMul.from(Constant.from(3), x));
        assertEquals(f3.toString(), "2^(3x)");
    }

    @Test
    public void test_hard_trigonometry() {
        Function f4 = TrigonometricFunction.from(FunctionSum.from(
                FunctionMul.from(Constant.from(2), x),
                FunctionMul.from(Constant.from(3), PowerFunction.from(x, 2))
        ), true);
        assertEquals(f4.toString(), "sin(2x + 3(x^2))");
    }
}
