package com.bobisonfire.system;

public class LogOperator implements Log {
    private final Ln ln;

    public LogOperator(Ln ln) {
        this.ln = ln;
    }

    @Override
    public double get(double base, double x, double precision) {
        return ln.get(x, precision / 10) / ln.get(base, precision / 10);
    }
}
