package com.bobisonfire.function;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiplyFunctionTest {
    private final Function x = Variable.from("x");
    private final Function add = FunctionSum.from(x, Constant.from(2));


    @Test
    public void test_exponent_sums_powers() {
        Function f1 = FunctionMul.from(
                ExponentialFunction.from(3, x),
                ExponentialFunction.from(3, add)
        );
        assertEquals(f1.toString(), "3^(2x + 2)");
    }

    @Test
    public void test_applicable_grouping() {
        Function f2 = FunctionMul.from(
                PowerFunction.from(add, 3),
                x,
                PowerFunction.from(add, 2)
        );
        assertEquals(f2.toString(), "((x + 2)^5)x");
    }

    @Test
    public void test_equal_grouping() {
        Function f3 = FunctionMul.from(x, x, x, x);
        assertEquals(f3.toString(), "x^4");
    }
}
