package com.bobisonfire.gauss;

public class SquareMatrix extends Matrix {
    private SquareMatrix triangleMatrix = null;

    public static SquareMatrix from(Rational[][] model) {
        SquareMatrix m = (SquareMatrix) Matrix.from(model); // todo test this

        if (m.cols != m.rows) throw new RuntimeException();
        return m;
    }

    public SquareMatrix getTriangleMatrix() {
        if (triangleMatrix != null) return triangleMatrix;
        Matrix m = this;
        for (int i = 0; i < rows - 1; i++) {
            int j = i + 1;
            while (j < rows && m.model[i][j].equals(Rational.ZERO)) j++;
            if (j == rows) continue;

            m = m.swapRows(i, j);

            for (j = i + 1; j < rows; j++) {
                if (m.model[j][i].equals(Rational.ZERO)) continue;
                Rational mul = m.model[j][i].divide(m.model[i][i]).negate();
                m = m.addCol(i, j, mul);
            }
        }

        triangleMatrix = (SquareMatrix) m;
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

        for (int i = 0; i < rows; i++) {
            if (t.model[i][i].equals(Rational.ZERO)) return Rational.ZERO;
            det = det.multiply(t.model[i][i]);
        }
        return det;
    }
}
