package com.bobisonfire.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DerivativeFunctionTest {
    private final Function x = Variable.from("x");

    @Test
    public void test_variable_derivative() {
        assertEquals(x.getDerivative("x").toString(), "1");
    }

    @Test
    public void test_exponent_derivative() {
        Function f1 = ExponentialFunction.from(Math.E, x);
        assertEquals(f1.getDerivative("x").toString(), "e^x");
    }

    @Test
    public void test_power_complicated_derivative() {
        Function f2 = PowerFunction.from(FunctionSum.from(x, Constant.from(2)), 6);
        assertEquals(f2.getDerivative("x").toString(), "6((x + 2)^5)");
    }

    @Test
    public void test_multiply_and_trigonometry_derivative() {
        Function f3 = FunctionMul.from(
                PowerFunction.from(x, 2),
                TrigonometricFunction.from(x, false)
        );
        assertEquals(f3.getDerivative("x").toString(), "2xcos(x) - (x^2)sin(x)");
    }

    @Test
    public void test_layered_function_derivative() {
        Function f4 = PowerFunction.from(TrigonometricFunction.from(x, true), 3);
        assertEquals(f4.getDerivative("x").toString(), "3((sin(x))^2)cos(x)");
    }
}
