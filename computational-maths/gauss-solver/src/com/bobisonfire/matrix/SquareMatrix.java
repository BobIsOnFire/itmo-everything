package com.bobisonfire.matrix;

public class SquareMatrix extends Matrix {
    private SquareMatrix triangleMatrix = null;
    private boolean negated = false;

    public static SquareMatrix from(Rational[][] model) {
        if (model.length == 0 || model[0].length == 0) throw new RuntimeException(); // todo: replace with custom exception
        if (model.length != model[0].length) throw new RuntimeException();

        SquareMatrix m = new SquareMatrix();

        m.model = model;
        m.rows = model.length;
        m.cols = model[0].length;

        return m;
    }

    public static SquareMatrix from(Matrix matrix) {
        return from(matrix.getModel());
    }

    public SquareMatrix getTriangleMatrix() {
        if (triangleMatrix != null) return triangleMatrix;
        Matrix m = this;
        for (int i = 0; i < rows - 1; i++) {
            int j = i;
            while (j < rows && m.model[i][j].equals(Rational.ZERO)) j++;
            if (j == rows) continue;

            if (i != j) {
                m = m.swapRows(i, j);
                negated = !negated;
            }

            for (j = i + 1; j < rows; j++) {
                if (m.model[j][i].equals(Rational.ZERO)) continue;
                Rational mul = m.model[j][i].divide(m.model[i][i]).negate();
                m = m.addRow(i, j, mul);
            }
        }

        triangleMatrix = SquareMatrix.from(m);
        return triangleMatrix;
    }

    public int getRank() {
        SquareMatrix t = getTriangleMatrix();
        int rank = rows;
        for (int i = 0; i < rows; i++) {
            if (t.model[i][i].equals(Rational.ZERO)) rank--;
        }
        return rank;
    }

    public Rational getDeterminant() {
        SquareMatrix t = getTriangleMatrix();
        Rational det = Rational.ONE;
        if (negated) det = det.negate();

        for (int i = 0; i < rows; i++) {
            if (t.model[i][i].equals(Rational.ZERO)) return Rational.ZERO;
            det = det.multiply(t.model[i][i]);
        }
        return det;
    }
}
