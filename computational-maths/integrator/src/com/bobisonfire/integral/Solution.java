package com.bobisonfire.integral;

public class Solution {
    private double integral;
    private double error;
    private int segments;

    public double getIntegral() {
        return integral;
    }

    public void setIntegral(double integral) {
        this.integral = integral;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

    public int getSegments() {
        return segments;
    }

    public void setSegments(int segments) {
        this.segments = segments;
    }

    @Override
    public String toString() {
        return "Result: " + integral + "\n" + "Segments: " + segments + "\n" + "Error: " + error;
    }
}
