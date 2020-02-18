package com.bobisonfire.test;

import com.bobisonfire.gauss.GaussSolver;
import com.bobisonfire.gauss.matrix.Matrix;
import com.bobisonfire.gauss.matrix.Rational;
import com.bobisonfire.gauss.solution.Solution;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SolverTest {
    @Test
    void oneSolution() {
        Rational[][] model = {
                {Rational.from(2), Rational.ONE, Rational.ONE, Rational.from(2)},
                {Rational.ONE, Rational.from(-1), Rational.ZERO, Rational.from(-2)},
                {Rational.from(3), Rational.from(-1), Rational.from(2), Rational.from(2)}
        };

        Rational[][] triangle = {
                {Rational.from(2), Rational.ONE, Rational.ONE, Rational.from(2)},
                {Rational.ZERO, Rational.from(-3, 2), Rational.from(-1, 2), Rational.from(-3)},
                {Rational.ZERO, Rational.ZERO, Rational.from(4, 3), Rational.from(4)}
        };

        Rational[][] diagonal = {
                {Rational.ONE, Rational.ZERO, Rational.ZERO, Rational.from(-1)},
                {Rational.ZERO, Rational.ONE, Rational.ZERO, Rational.ONE},
                {Rational.ZERO, Rational.ZERO, Rational.ONE, Rational.from(3)}
        };

        Rational[] freeMembers = {Rational.from(-1), Rational.ONE, Rational.from(3)};

        GaussSolver solver = new GaussSolver( Matrix.from(model) );

        assertEquals(solver.getTriangleMatrix(), Matrix.from(triangle));
        assertEquals(solver.getDeterminant(), Rational.from(-4));
        assertEquals(solver.getRank(), 3);
        assertEquals(solver.getPartlyDiagonalMatrix(), Matrix.from(diagonal));

        Solution solution = solver.getSolution();
        System.out.println(solution);
        assertArrayEquals(solution.getFreeMembers(), freeMembers);
    }

    @Test
    void infiniteSolutions() {
        Rational[][] model = {
                {Rational.from(2), Rational.ONE, Rational.from(-1), Rational.from(4)},
                {Rational.ONE, Rational.from(-1), Rational.ZERO, Rational.from(5)},
        };

        Rational[][] triangle = {
                {Rational.from(2), Rational.ONE, Rational.from(-1), Rational.from(4)},
                {Rational.ZERO, Rational.from(-3, 2), Rational.from(1, 2), Rational.from(3)},
        };

        Rational[][] diagonal = {
                {Rational.ONE, Rational.ZERO, Rational.from(-1, 3), Rational.from(3)},
                {Rational.ZERO, Rational.ONE, Rational.from(-1, 3), Rational.from(-2)},
        };

        Rational[] freeMembers = {Rational.from(3), Rational.from(-2), null};

        GaussSolver solver = new GaussSolver( Matrix.from(model) );

        assertEquals(solver.getTriangleMatrix(), Matrix.from(triangle));
        assertEquals(solver.getDeterminant(), Rational.from(-3));
        assertEquals(solver.getRank(), 2);
        assertEquals(solver.getPartlyDiagonalMatrix(), Matrix.from(diagonal));

        Solution solution = solver.getSolution();
        System.out.println(solution);
        assertArrayEquals(solution.getFreeMembers(), freeMembers);

    }
}