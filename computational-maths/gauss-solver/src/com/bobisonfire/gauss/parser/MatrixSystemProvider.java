package com.bobisonfire.gauss.parser;

import com.bobisonfire.gauss.matrix.Rational;

import java.util.Scanner;

public class MatrixSystemProvider extends SystemProvider {
    @Override
    public Rational[][] parseModel(Scanner scanner) {
        Rational[][] model = new Rational[size][size + 1];

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

        return model;
    }

    @Override
    protected String[] getVariableNames() {
        String[] variableNames = new String[size];
        for (int i = 0; i < size; i++) variableNames[i] = "x" + (i + 1);
        return variableNames;
    }
}
