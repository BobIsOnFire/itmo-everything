package com.bobisonfire.gauss.parser;

import com.bobisonfire.gauss.GaussSolver;
import com.bobisonfire.gauss.matrix.Matrix;
import com.bobisonfire.gauss.matrix.Rational;
import com.bobisonfire.gauss.solution.Solution;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public abstract class SystemProvider {
    private Rational[][] model;
    protected int size;

    protected abstract Rational[][] parseModel(Scanner scanner);
    protected abstract String[] getVariableNames();

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

        size = Integer.parseInt(line);
        model = parseModel(scanner);
    }


    public void printSolution(PrintStream out) {
        Matrix matrix = Matrix.from(model);
        out.println("Original matrix:\n" + matrix + "\n");

        GaussSolver solver = new GaussSolver(matrix);
        out.println("Determinant: " + solver.getDeterminant());
        out.println("Triangle matrix:\n" + solver.getTriangleMatrix() + "\n");

        Solution solution = solver.getSolution( getVariableNames() );
        out.println("Solution:\n" + solution);

        if (solution.hasSolutions()) {
            out.println("Variables: " + Arrays.toString(solution.getVariableNames()));
            out.println("Remainders: " + Arrays.toString(solution.getRemainders()));
        }
    }
}
