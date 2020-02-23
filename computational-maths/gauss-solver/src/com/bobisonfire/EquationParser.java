package com.bobisonfire;

import com.bobisonfire.gauss.GaussSolver;
import com.bobisonfire.gauss.matrix.Matrix;
import com.bobisonfire.gauss.matrix.Rational;
import com.bobisonfire.gauss.solution.Solution;

import java.io.InputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class EquationParser implements Parser {
    private Rational[][] model;
    private String[] variableNames;

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
        variableNames = new String[size];

        for (Rational[] row : model)
            for (int i = 0; i <= size; i++)
                row[i] = Rational.ZERO;

        try {
            int variablePointer = 0;
            for (int i = 0; i < size; i++) {
                if (!scanner.hasNextLine()) throw new IndexOutOfBoundsException();

                boolean parsingNumber = false;
                boolean parsingVariable = false;
                boolean hadNumberInBlock = false;
                boolean hadVariableInBlock = false;
                boolean hadSeparatorInNumber = false;

                StringBuilder numberBuilder = new StringBuilder();
                StringBuilder variableBuilder = new StringBuilder();

                List<Rational> numbers = new ArrayList<>();
                List<String> variables = new ArrayList<>();

                String equation = scanner.nextLine().trim().replaceAll("\\s+", " ").concat("+");
                int equalSignOffset = -1;
                int charOffset = -1;

                for (char ch : equation.toCharArray()) {
                    charOffset++;

                    if (ch == ' ') {
                        if (parsingNumber) {
                            parsingNumber = false;
                            hadNumberInBlock = true;
                        }

                        if (parsingVariable) {
                            parsingVariable = false;
                            hadVariableInBlock = true;
                        }

                        continue;
                    }

                    if (ch >= '0' && ch <= '9') {
                        if (!parsingNumber) {
                            if (!parsingVariable && !hadNumberInBlock) parsingNumber = true;
                            if (hadNumberInBlock && !parsingVariable || hadVariableInBlock) throw new ParseException(equation, charOffset);
                        }

                        if (parsingNumber) numberBuilder.append(ch);
                        if (parsingVariable) variableBuilder.append(ch);

                        continue;
                    }

                    if (ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z') {
                        if (!parsingVariable) {
                            if (parsingNumber) {
                                parsingNumber = false;
                                hadNumberInBlock = true;
                            }

                            if (hadVariableInBlock) throw new ParseException(equation, charOffset);
                            parsingVariable = true;
                        }

                        variableBuilder.append(ch);
                        continue;
                    }

                    if (ch == '_') {
                        if (!parsingVariable) throw new ParseException(equation, charOffset);
                        variableBuilder.append(ch);
                        continue;
                    }

                    if (ch == '.') {
                        if (hadSeparatorInNumber) throw new ParseException(equation, charOffset);

                        if (!parsingNumber) {
                            if (hadNumberInBlock) throw new ParseException(equation, charOffset);
                            parsingNumber = true;
                            hadSeparatorInNumber = true;
                        }

                        numberBuilder.append(ch);
                        continue;
                    }

                    if (ch == '/') {
                        if (!parsingNumber || hadSeparatorInNumber) throw new ParseException(equation, charOffset);
                        numberBuilder.append(ch);
                        continue;
                    }

                    if (ch == '+' || ch == '-' || ch == '=') {
                        if (parsingNumber) {
                            parsingNumber = false;
                            hadNumberInBlock = true;
                        }

                        if (parsingVariable) {
                            parsingVariable = false;
                            hadVariableInBlock = true;
                        }

                        if (ch == '-' && !hadVariableInBlock && numberBuilder.length() == 0) {
                            numberBuilder.append(ch);
                            continue;
                        }

                        if (ch == '=' && equalSignOffset != -1) throw new ParseException(equation, charOffset);
                        if (!hadNumberInBlock && !hadVariableInBlock) throw new ParseException(equation, charOffset);

                        if (!hadNumberInBlock) numberBuilder.append('1');
                        numbers.add(Rational.parse( numberBuilder.toString() ));
                        variables.add(variableBuilder.toString());
                        if (ch == '=') equalSignOffset = numbers.size();

                        parsingNumber = false;
                        parsingVariable = false;
                        hadNumberInBlock = false;
                        hadVariableInBlock = false;
                        hadSeparatorInNumber = false;

                        numberBuilder = new StringBuilder();
                        variableBuilder = new StringBuilder();

                        if (ch == '-') numberBuilder.append('-');
                        continue;
                    }

                    throw new ParseException(equation, charOffset);
                }

                if (equalSignOffset == -1) throw new ParseException(equation, charOffset);

                for (int j = 0; j < numbers.size(); j++) {
                    String var = variables.get(j);
                    Rational r = numbers.get(j);
                    if (var.isEmpty()) {
                        if (j < equalSignOffset) r = r.negate();
                        model[i][size] = model[i][size].add(r);
                        continue;
                    }

                    int index = variableIndexOf(var);
                    if (index == -1) {
                        index = variablePointer++;
                        variableNames[index] = var;
                    }

                    if (j >= equalSignOffset) r = r.negate();
                    model[i][index] = model[i][index].add(r);
                }
            }

            if (variablePointer != size) throw new IndexOutOfBoundsException();
        } catch (IndexOutOfBoundsException exc) {
            System.out.println("Expected size is not equal to the size of parsed matrix.");
            System.exit(0);
        } catch (NumberFormatException exc) {
            System.out.println("Found non-rational value in equation. " + exc.getMessage());
            System.exit(0);
        } catch (ParseException exc) {
            System.out.println("Cannot parse equation: " + exc.getMessage() + ", offset - " + exc.getErrorOffset());
            System.exit(0);
        }
    }

    private int variableIndexOf(String var) {
        int k = 0;
        while (k < variableNames.length && !var.equals(variableNames[k])) k++;

        if (k == variableNames.length) return -1;
        return k;
    }

    @Override
    public void printSolution(PrintStream out) {
        Matrix matrix = Matrix.from(model);
        out.println("Original matrix:\n" + matrix + "\n");

        GaussSolver solver = new GaussSolver(matrix);
        out.println("Determinant: " + solver.getDeterminant());
        out.println("Triangle matrix:\n" + solver.getTriangleMatrix() + "\n");

        Solution solution = solver.getSolution(variableNames);
        out.println("Solution:\n" + solution);

        if (!solution.isNoSolutions()) {
            out.println("Variables: " + Arrays.toString(solution.getVariableNames()));
            out.println("Remainders: " + Arrays.toString(solution.getRemainders()));
        }
    }
}
