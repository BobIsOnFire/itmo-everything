package com.bobisonfire.gauss.parser;

import com.bobisonfire.gauss.matrix.Rational;

import java.util.Random;
import java.util.Scanner;

public class RandomSystemProvider extends SystemProvider {

    @Override
    protected Rational[][] parseModel(Scanner scanner) {
        Rational[][] model = new Rational[size][size + 1];
        Random r = new Random(System.currentTimeMillis());
        r.nextInt();
        r.nextInt();
        r.nextInt();

        for (Rational[] row : model) {
            for (int i = 0; i < size + 1; i++) {
                row[i] = Rational.from(r.nextInt(100), r.nextInt(100));
            }
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
