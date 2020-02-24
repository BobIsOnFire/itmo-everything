package com.bobisonfire;

import com.bobisonfire.gauss.parser.EquationParser;
import com.bobisonfire.gauss.parser.MatrixParser;
import com.bobisonfire.gauss.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        boolean isMatrix = false;
        boolean isEquation = false;
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

        if (!isEquation && !isMatrix) System.out.println("No mode specified - entering matrix mode as default.");

        Parser parser;
        if (isEquation) parser = new EquationParser();
        else parser = new MatrixParser();

        try {
            InputStream in = filePath == null ? System.in : Files.newInputStream(filePath);
            parser.readAndParse(in);
            parser.printSolution(System.out);
        }
        catch (IOException exc) {
            System.out.println("Cannot open specified file.");
            System.exit(0);
        }
    }


}
