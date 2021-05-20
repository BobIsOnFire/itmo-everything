package com.bobisonfire.system;

import static java.lang.Math.*;

public class FunctionSystemOperator implements FunctionSystem {
    private final Cos cos;
    private final Csc csc;
    private final Ln ln;
    private final Log log;

    public FunctionSystemOperator(Cos cos, Csc csc, Ln ln, Log log) {
        this.cos = cos;
        this.csc = csc;
        this.ln = ln;
        this.log = log;
    }

    @Override
    public double get(double x, double precision) {
        double p = precision / 1000;
        if (x <= 0) {
            return cos.get(x, p) * cos.get(x, p) - pow(csc.get(x, p), 3);
        }

        double arg1 = pow( pow(ln.get(x, p), 2) / log.get(5, x, p), 2 );
        double arg2 = log.get(10, x, p);
        double arg3 = log.get(3, x, p);
        double arg4 = log.get(5, x, p) / ln.get(x, p) * log.get(10, x, p) / log.get(5, x, p);

        return (arg1 + arg2) + (arg3 + arg4);
    }
}
