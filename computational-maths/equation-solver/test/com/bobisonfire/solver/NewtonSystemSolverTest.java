package com.bobisonfire.solver;

import com.bobisonfire.function.Function;
import com.bobisonfire.function.Point;
import com.bobisonfire.parser.FunctionParser;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class NewtonSystemSolverTest {
    @Test
    public void test_primitive() {
        try {
            Function f1 = new FunctionParser().parse("x + y + 3");
            Function f2 = new FunctionParser().parse("2x - y");

            NewtonSystemSolver solver = new NewtonSystemSolver(f1, f2);
            SystemSolution solution = solver.getSolution(-100, 100, 0.0001, 1);
            assertTrue(isPrecise(solution.getRoots(), new Point[] { Point.fromJSON("x: -1, y: -2") }, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @Test
    public void test_not_so_primitive() {
        try {
            Function f1 = new FunctionParser().parse("sin(2x - y) - 1.2x - 0.4");
            Function f2 = new FunctionParser().parse("0.8 x^2 + 1.5 y^2 - 1");

            NewtonSystemSolver solver = new NewtonSystemSolver(f1, f2);
            SystemSolution solution = solver.getSolution(-3, 3, 0.0001, 0.5);

            Point[] check = new Point[] {
                    Point.fromJSON("x: 0.49124, y: -0.73346"),
                    Point.fromJSON("x: -1.09059, y: -0.17978")
            };

            assertTrue(isPrecise(solution.getRoots(), check, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @Test
    public void test_precision() {
        try {
            Function f1 = new FunctionParser().parse("x");
            Function f2 = new FunctionParser().parse("2x - y");

            NewtonSystemSolver solver = new NewtonSystemSolver(f1, f2);
            SystemSolution solution = solver.getSolution(-3, 3, 0.0001, 1);

            Point[] check = new Point[] {
                    Point.fromJSON("x: 0, y: 0")
            };

            assertTrue(isPrecise(solution.getRoots(), check, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    @Test
    public void test_weird_lags() {
        try {
            Function f1 = new FunctionParser().parse("x + y");
            Function f2 = new FunctionParser().parse("0");

            NewtonSystemSolver solver = new NewtonSystemSolver(f1, f2);
            SystemSolution solution = solver.getSolution(-3, 3, 0.0001, 1);

            Point[] check = new Point[] {
                    Point.fromJSON("x: 3, y: -3"),
                    Point.fromJSON("x: 2, y: -2"),
                    Point.fromJSON("x: 1, y: -1"),
                    Point.fromJSON("x: 0, y: 0"),
                    Point.fromJSON("x: -1, y: 1"),
                    Point.fromJSON("x: -2, y: 2"),
                    Point.fromJSON("x: -3, y: 3")
            };

            assertTrue(isPrecise(solution.getRoots(), check, 0.0001));
        } catch (ParseException e) {
            fail(e);
        }
    }

    private boolean isPrecise(Point[] roots, Point[] check, double precision) {
        if (roots.length != check.length) return false;

        for (int i = 0; i < roots.length; i++) {
            if (!roots[i].isPrecise(check[i], precision)) return false;
        }

        return true;
    }
}
