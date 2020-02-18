package com.bobisonfire.test;

import com.bobisonfire.gauss.GaussSolver;
import com.bobisonfire.gauss.matrix.Matrix;
import com.bobisonfire.gauss.matrix.Rational;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTransformTest {

    private Rational[][] starter = {
            {Rational.from(3), Rational.from(3), Rational.from(-1)},
            {Rational.from(4), Rational.ONE, Rational.from(3)},
            {Rational.ONE, Rational.from(-2), Rational.from(-2)}
    };

    private Rational[][] triangle = {
            {Rational.from(3), Rational.from(3), Rational.from(-1)},
            {Rational.ZERO, Rational.from(-3), Rational.from(13, 3)},
            {Rational.ZERO, Rational.ZERO, Rational.from(-6)}
    };

    private Matrix matrix = Matrix.from(starter);
    private GaussSolver solver = new GaussSolver(matrix);

    @Test
    void from() {
        Rational[][] model = matrix.getModel();
        for (int i = 0; i < model.length; i++)
            assertArrayEquals(model[i], starter[i]);
    }

    @Test
    void getTriangleMatrix() {
        Rational[][] model = solver.getTriangleMatrix().getModel();
        for (int i = 0; i < model.length; i++)
            assertArrayEquals(model[i], triangle[i]);
    }

    @Test
    void getRank() {
        assertEquals(solver.getRank(), 3);
    }

    @Test
    void getDeterminant() {
        assertEquals(solver.getDeterminant(), Rational.from(54));
    }


}