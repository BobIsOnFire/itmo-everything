package com.bobisonfire.parser;

import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void test_basic_sum() {
        try {
            assertEquals(new FunctionParser().parse("x + 1").toString(), "x + 1");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_basic_power() {
        try {
            assertEquals(new FunctionParser().parse("x^2").toString(), "x^2");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_basic_exponent() {
        try {
            assertEquals(new FunctionParser().parse("e^x").toString(), "e^x");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_basic_trigonometry() {
        try {
            assertEquals(new FunctionParser().parse("sinx").toString(), "sin(x)");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_basic_mul() {
        try {
            assertEquals(new FunctionParser().parse("xy").toString(), "xy");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_layers() {
        try {
            assertEquals(new FunctionParser().parse("(x + y)^2").toString(), "(x + y)^2");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_arithmetics_for_some_reason() {
        try {
            assertEquals(new FunctionParser().parse("2 + 2").toString(), "4");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_applicable_sum() {
        try {
            assertEquals(new FunctionParser().parse("3 x + 2 + x").toString(), "4x + 2");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_layered_trigonometry() {
        try {
            assertEquals(new FunctionParser().parse("sin(x^2 + 3 x + 2)").toString(), "sin(x^2 + 3x + 2)");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_algebra_for_some_reason() {
        try {
            assertEquals(new FunctionParser().parse("sinp + cosp").toString(), "-1.0");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_bracket_management() {
        try {
            assertEquals(new FunctionParser().parse("(sin(t+2))^2").toString(), "(sin(t + 2))^2");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_power_sum_in_exponents() {
        try {
            assertEquals(new FunctionParser().parse("2^(sint + 3 x) * 2^x").toString(), "2^(sin(t) + 4x)");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void test_different_multipliers() {
        try {
            assertEquals(new FunctionParser().parse("x^2 * y^2").toString(), "(x^2)(y^2)");
        } catch (ParseException e) {
            e.printStackTrace();
            fail();
        }
    }
}
