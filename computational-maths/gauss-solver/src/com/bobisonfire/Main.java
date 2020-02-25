package com.bobisonfire;

import com.bobisonfire.gauss.parser.EquationSystemProvider;
import com.bobisonfire.gauss.parser.MatrixSystemProvider;
import com.bobisonfire.gauss.parser.SystemProvider;
import com.bobisonfire.gauss.parser.RandomSystemProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        boolean isMatrix = false;
        boolean isEquation = false;
        boolean isRandom = false;
        Path filePath = null;

        for (String s : args) {
            String arg = s.trim();
            if (!isMatrix && ("-m".equals(arg) || "--matrix".equals(arg))) {
                isMatrix = true;
                continue;
            }

            if (!isEquation && ("-e".equals(arg) || "--equation".equals(arg))) {
                isEquation = true;
                continue;
            }

            if (!isRandom && ("-r".equals(arg) || "--random".equals(arg))) {
                isRandom = true;
                continue;
            }

            if (filePath != null) {
                System.out.println("Too many arguments.");
                System.exit(0);
            }

            filePath = Paths.get(arg);
        }

        if (isEquation && isMatrix) {
            System.out.println("Incorrect key combination.");
            System.exit(0);
        }

        if (!isEquation && !isMatrix && !isRandom) System.out.println("No mode specified - entering matrix mode as default.");

        SystemProvider systemProvider;
        if (isEquation) systemProvider = new EquationSystemProvider();
        else if (isMatrix) systemProvider = new MatrixSystemProvider();
        else systemProvider = new RandomSystemProvider();

        try {
            InputStream in = filePath == null ? System.in : Files.newInputStream(filePath);

            if (filePath == null) System.out.println("System size:");
            systemProvider.readAndParse(in);
            systemProvider.printSolution(System.out);
        }
        catch (IOException exc) {
            System.out.println("Cannot open specified file.");
            System.exit(0);
        }
    }


}
