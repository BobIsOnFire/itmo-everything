package com.bobisonfire.functions;

public class SinProvider extends FunctionProvider {
    @Override
    public double getLeftValue(double x) {
        return Math.sin(x);
    }

    @Override
    public String getDescription() {
        return "sin(x)";
    }
}
