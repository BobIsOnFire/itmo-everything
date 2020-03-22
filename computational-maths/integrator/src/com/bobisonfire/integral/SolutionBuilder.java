package com.bobisonfire.integral;

public class SolutionBuilder {
    private Solution solution;

    public static SolutionBuilder instance() {
        SolutionBuilder builder = new SolutionBuilder();
        builder.solution = new Solution();
        return builder;
    }

    public SolutionBuilder integral(double integral) {
        this.solution.setIntegral(integral);
        return this;
    }

    public SolutionBuilder error(double error) {
        this.solution.setError(error);
        return this;
    }

    public SolutionBuilder segments(int segments) {
        this.solution.setSegments(segments);
        return this;
    }

    public Solution get() {
        return solution;
    }
}
