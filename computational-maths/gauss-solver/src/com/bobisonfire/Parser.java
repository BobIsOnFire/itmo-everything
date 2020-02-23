package com.bobisonfire;

import java.io.InputStream;
import java.io.PrintStream;

public interface Parser {
    void readAndParse(InputStream in);
    void printSolution(PrintStream out);
}
