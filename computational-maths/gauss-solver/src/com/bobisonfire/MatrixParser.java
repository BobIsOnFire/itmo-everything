package com.bobisonfire;

import com.bobisonfire.gauss.GaussSolver;
import com.bobisonfire.gauss.matrix.Matrix;
import com.bobisonfire.gauss.matrix.Rational;
import com.bobisonfire.gauss.solution.Solution;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class MatrixParser implements Parser {
    private Rational[][] model;

    @Override
    public void readAndParse(InputStream in) {
        Scanner scanner = new Scanner(in);

        if (!scanner.hasNextLine()) {
            System.out.println("Heyy what's up with your input stream? I reached unexpected EOF. Gimme yo' bytes!");
            System.exit(0);
        }

        String line = scanner.nextLine().trim();
        if (!line.matches("-?\\d+")) {
            System.out.println("First line should contain the size of system.");
            System.exit(0);
        }

        int size = Integer.parseInt(line);
        model = new Rational[size][size + 1];

        try {
            for (int i = 0; i < size; i++) {
                if (!scanner.hasNextLine()) throw new IndexOutOfBoundsException();

                String[] tokens = scanner.nextLine().trim().split("\\s+\\|?\\s*");
                for (int j = 0; j < size + 1; j++) {
                    String s = tokens[j].trim();
                    model[i][j] = Rational.parse(s);
                }
            }
        } catch (IndexOutOfBoundsException exc) {
            System.out.println("Matrix size is less than expected size.");
            System.exit(0);
        } catch (NumberFormatException exc) {
            System.out.println("Found non-rational value in matrix. " + exc.getMessage());
            System.exit(0);
        }
    }

    @Override
    public void printSolution(PrintStream out) {
        Matrix matrix = Matrix.from(model);
        out.println("Original matrix:\n" + matrix + "\n");

        GaussSolver solver = new GaussSolver(matrix);
        out.println("Determinant: " + solver.getDeterminant());
        out.println("Triangle matrix:\n" + solver.getTriangleMatrix() + "\n");

        Solution solution = solver.getSolution();
        out.println("Solution:\n" + solution);

        if (!solution.isNoSolutions()) {
            out.println("Variables: " + Arrays.toString(solution.getVariableNames()));
            out.println("Remainders: " + Arrays.toString(solution.getRemainders()));
        }
    }
}
