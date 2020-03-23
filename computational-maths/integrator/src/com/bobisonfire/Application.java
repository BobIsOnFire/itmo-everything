package com.bobisonfire;

import com.bobisonfire.integral.IntegralException;
import com.bobisonfire.integral.Solution;
import com.bobisonfire.integral.TrapezoidSolver;
import com.bobisonfire.functions.*;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Application {
    private static final FunctionProvider[] providers = {
            new PolynomProvider(), new FractionProvider(), new FloorProvider(),
            new SinProvider(), new LnProvider()
    };

    public static void start() {
        System.out.println("Computational Mathematics Lab#2 by Nikita Akatyev.");
        System.out.println("Calculating integrals, trapezoid method.");
        System.out.println();
        printFunctions();
        System.out.println("\nUse ? or help for list of commands.");

        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            if (!sc.hasNextLine()) break;

            String command = sc.nextLine().trim();
            if (command.isEmpty()) continue;
            if (command.equals("exit")) break;

            if (command.equals("?") || command.equals("help")) {
                printHelp();
                continue;
            }

            if (command.equals("f") || command.equals("functions")) {
                printFunctions();
                continue;
            }

            String[] tokens = command.split("\\s+");
            if (!tokens[0].equals("int")) {
                System.out.println("Invalid command.");
                continue;
            }

            if (tokens.length < 2) {
                System.out.println("Enter the second argument.");
                continue;
            }

            try {
                int num = Integer.parseInt(tokens[1]);
                if (num <= 0 || num > providers.length) {
                    System.out.println("Enter a valid number of function in the list.");
                    continue;
                }

                TrapezoidSolver solver = new TrapezoidSolver(providers[num - 1]);

                double a = readDouble(sc, "\tLow limit: ");
                double b = readDouble(sc, "\tHigh limit: ");
                double precision = readDouble(sc, "\tPrecision: ");

                System.out.println("Function " + providers[num - 1].getDescription());
                Solution solution = solver.integrate(a, b, precision);
                System.out.println(solution);
            }
            catch (NumberFormatException exc) {
                System.out.println("Enter a valid numeric argument.");
            }
            catch (IntegralException exc) {
                System.out.println(exc.getMessage());
            }
            catch (NoSuchElementException | IllegalStateException exc) {
                break;
            }
        }
    }

    private static void printHelp() {
        System.out.println("*** Commands ***");
        System.out.println("\t? or help - show this message");
        System.out.println("\tf or functions - list all functions");
        System.out.println("\tint n - calculate integral for n-th function in the list");
        System.out.println("\texit - exit the program.");
    }

    private static void printFunctions() {
        System.out.println("*** Available functions ***");
        for (int i = 0; i < providers.length; i++) System.out.println("\t" + (i + 1) + ". " + providers[i].getDescription());
    }

    private static double readDouble(Scanner sc, String hint) {
        while (true) {
            try {
                System.out.print(hint);
                return Double.parseDouble(sc.nextLine());
            } catch (NumberFormatException exc) {
                System.out.println("Enter a valid numeric argument.");
            }
        }
    }
}
