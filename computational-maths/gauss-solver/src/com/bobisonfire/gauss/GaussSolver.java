package com.bobisonfire.gauss;

import com.bobisonfire.gauss.solution.Solution;
import com.bobisonfire.gauss.solution.SolutionBuilder;
import com.bobisonfire.gauss.matrix.Matrix;
import com.bobisonfire.gauss.matrix.Rational;

public class GaussSolver {
    private Matrix matrix;
    private Matrix triangleMatrix = null;

    private int rows;
    private int cols;

    private boolean negated = false;
    private boolean[] isZero;

    public GaussSolver(Matrix matrix) {
        this.matrix = matrix;
        this.rows = matrix.getModel().length;
        this.cols = matrix.getRow(0).length;

        this.isZero = new boolean[cols - 1];
        for (int i = 0; i < cols - 1; i++) isZero[i] = true;
    }

    public Matrix getTriangleMatrix() {
        if (triangleMatrix != null) return triangleMatrix;
        Matrix t = matrix;

        int steps = Math.min(cols, rows);

        for (int i = 0; i < steps; i++) {
            int j = i;
            while (j < rows && t.get(i, j).equals(Rational.ZERO)) j++;
            if (j == rows) continue;

            if (i != j) {
                t = t.swapRows(i, j);
                negated = !negated;
            }

            isZero[i] = false;
            for (j = i + 1; j < rows; j++) {
                if (t.get(j, i).equals(Rational.ZERO)) continue;
                Rational mul = t.get(j, i).divide( t.get(i, i) ).negate();
                t = t.addRow(i, j, mul);
            }
        }

        triangleMatrix = t;
        return triangleMatrix;
    }

    public Rational getDeterminant() { // todo: check if rows > cols
        Matrix t = getTriangleMatrix();
        Rational det = Rational.ONE;
        if (negated) det = det.negate();

        for (int i = 0; i < rows; i++) {
            if ( t.get(i, i).equals(Rational.ZERO) ) return Rational.ZERO;
            det = det.multiply( t.get(i, i) );
        }
        return det;
    }

    public int getRank() {
        Matrix t = getTriangleMatrix();
        int rank = rows;

        for (int i = 0; i < rows; i++) {
            if ( t.get(i, i).equals(Rational.ZERO) ) rank--;
        }
        return rank;
    }

    public Matrix getPartlyDiagonalMatrix() {
        Matrix t = getTriangleMatrix();

        for (int i = rows - 1; i >= 0; i--) {
            if (t.get(i, i).equals(Rational.ZERO)) continue;

            t = t.multiplyRow( i, t.get(i, i).reverse() );

            for (int j = i - 1; j >= 0; j--) {
                if ( t.get(j, i).equals(Rational.ZERO) ) continue;
                Rational mul = t.get(j, i).negate();
                t = t.addRow(i, j, mul);
            }
        }

        return t;
    }

    public Solution getSolution(String[] variableNames) {
        Matrix t = getPartlyDiagonalMatrix();

        Rational[] freeMembers = new Rational[cols - 1];
        Rational[][] constants = new Rational[rows][cols - 1];
        boolean infiniteSolutions = false;

        for (int i = 0; i < cols - 1; i++) {
            if (!isZero[i]) {
                freeMembers[i] = t.get(i, cols - 1);
                continue;
            }

            freeMembers[i] = Rational.ZERO;

            if ( i < rows && !t.get(i, cols - 1).equals(Rational.ZERO) )
                return SolutionBuilder.instance().noSolutions(true).get();

            infiniteSolutions = true;
            for (int j = 0; j < rows; j++) constants[j][i] = t.get(j, i).negate();
        }

        Rational[] remainders = new Rational[rows];
        for (int i = 0; i < rows; i++) {
            Rational sum = Rational.ZERO;
            for (int j = 0; j < cols - 1; j++) {
                sum = sum.add( freeMembers[j].multiply(matrix.get(i, j)) );
            }
            remainders[i] = matrix.get(i, cols - 1).subtract(sum);
        }

        SolutionBuilder builder = SolutionBuilder.instance()
                .freeMembers(freeMembers)
                .remainders(remainders)
                .variableNames(variableNames);

        if (!infiniteSolutions) {
            return builder.get();
        }

        return builder.infiniteSolutions(true)
                .isAny(isZero)
                .constants(constants)
                .get();
    }

    public Solution getSolution() {
        String[] variableNames = new String[cols - 1];
        for (int i = 0; i < cols - 1; i++) variableNames[i] = "x" + (i + 1);
        return getSolution(variableNames);
    }
}
