package com.bobisonfire.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SummarizeFunctionTest {
    private final Function x = Variable.from("x");

    @Test
    public void test_sum_applicable() {
        Function f1 = FunctionSum.from(x, FunctionMul.from(Constant.from(3), x));
        assertEquals(f1.toString(), "4x");
    }

    @Test
    public void test_sum_equal() {
        Function exp = ExponentialFunction.from(3, x);
        Function f2 = FunctionSum.from(exp, exp);
        assertEquals(f2.toString(), "2(3^x)");
    }

    @Test
    public void test_sum_complicated() {
        Function sin = TrigonometricFunction.from(x, true);
        Function f3 = FunctionSum.from(
                FunctionMul.from(Constant.from(0.1), sin),
                FunctionMul.from(Constant.from(-1), sin)
        );
        assertEquals(f3.toString(), " - 0.9sin(x)");
    }
}
