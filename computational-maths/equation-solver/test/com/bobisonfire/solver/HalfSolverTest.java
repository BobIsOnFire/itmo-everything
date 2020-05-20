package com.bobisonfire.solver;

import com.bobisonfire.function.Function;
import com.bobisonfire.parser.FunctionParser;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.*;

public class HalfSolverTest {
    @Test
    public void test_primitive() {
        try {
            Function f1 = new FunctionParser().parse("x + 1");
            EquationSolver solver = new HalfSolver(f1);
            Solution solution = solver.getSolution(-100, 100, 0.0001, 1);
            assertTrue(isPrecise(solution.getRoots(), new double[] { -1 }, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @Test
    public void test_fraction_root() {
        try {
            Function f2 = new FunctionParser().parse("3x + 2");
            EquationSolver solver = new HalfSolver(f2);
            Solution solution = solver.getSolution(-100, 100, 0.0001, 1);
            assertTrue(isPrecise(solution.getRoots(), new double[] { -2.0/3 }, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @Test
    public void test_bi_nom() {
        try {
            Function f3 = new FunctionParser().parse("x^2 - 3");
            EquationSolver solver = new HalfSolver(f3);
            Solution solution = solver.getSolution(-100, 100, 0.0001, 1);
            assertTrue(isPrecise(solution.getRoots(), new double[] { -Math.sqrt(3), Math.sqrt(3) }, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @Test
    public void test_trigonometry() {
        try {
            Function f4 = new FunctionParser().parse("sinx");
            EquationSolver solver = new HalfSolver(f4);
            Solution solution = solver.getSolution(-4, 4, 0.0001, 1);
            assertTrue(isPrecise(solution.getRoots(), new double[] { -Math.PI, 0, Math.PI }, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    private boolean isPrecise(double[] roots, double[] check, double precision) {
        if (roots.length != check.length) return false;

        for (int i = 0; i < roots.length; i++) {
            if (Math.abs(roots[i] - check[i]) > precision) return false;
        }

        return true;
    }
}
