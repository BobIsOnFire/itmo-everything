package com.bobisonfire;

import com.bobisonfire.system.*;

import java.io.IOException;
import java.nio.file.Path;

import static java.lang.Math.PI;

public class Main {
    public static void main(String[] args) {
        Cos cos = new CosOperator();
        Csc csc = new CscOperator(cos);
        Ln ln = new LnOperator();
        Log log = new LogOperator(ln);
        FunctionSystem system = new FunctionSystemOperator(cos, csc, ln, log);

        try {
            cos.saveValues(Path.of("cos_values.csv"), -2 * PI, 2 * PI, PI / 12);
            csc.saveValues(Path.of("csc_values.csv"), -2 * PI, 2 * PI, PI / 12);
            ln.saveValues(Path.of("ln_values.csv"), 0, 10, 0.1);
            log.saveValues(Path.of("log3_values.csv"), 3, 0, 10, 0.1);
            log.saveValues(Path.of("log5_values.csv"), 5, 0, 10, 0.1);
            log.saveValues(Path.of("log10_values.csv"), 10, 0, 10, 0.1);
            system.saveValues(Path.of("system_values.csv"), -10, 10, 0.01);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
