package com.bobisonfire.gauss.solution;

import com.bobisonfire.gauss.matrix.Rational;

public class Solution {
    private boolean infiniteSolutions;
    private boolean noSolutions;

    private boolean[] isAny;
    private Rational[] freeMembers;
    private Rational[][] constants;
    private Rational[] remainders;

    private String[] variableNames;

    public boolean isInfiniteSolutions() {
        return infiniteSolutions;
    }

    public boolean hasSolutions() {
        return !noSolutions;
    }

    public boolean[] getIsAny() {
        return isAny;
    }

    public Rational[] getFreeMembers() {
        return freeMembers;
    }

    public Rational[][] getConstants() {
        return constants;
    }

    public Rational[] getRemainders() {
        return remainders;
    }

    public String[] getVariableNames() {
        return variableNames;
    }

    void setInfiniteSolutions(boolean infiniteSolutions) {
        this.infiniteSolutions = infiniteSolutions;
    }

    void setNoSolutions(boolean noSolutions) {
        this.noSolutions = noSolutions;
    }

    void setIsAny(boolean[] isAny) {
        this.isAny = isAny;
    }

    void setFreeMembers(Rational[] freeMembers) {
        this.freeMembers = freeMembers;
    }

    void setConstants(Rational[][] constants) {
        this.constants = constants;
    }

    void setRemainders(Rational[] remainders) {
        this.remainders = remainders;
    }

    void setVariableNames(String[] variableNames) {
        this.variableNames = variableNames;
    }

    @Override
    public String toString() {
        if (noSolutions) return "No solutions.";

        int size = freeMembers.length;

        if (!infiniteSolutions) {
            StringBuilder sb = new StringBuilder("One solution:\n");
            for (int i = 0; i < size; i++)
                sb.append( String.format("%s = %s", variableNames[i], freeMembers[i]) ).append('\n');
            return sb.toString();
        }

        String[] constantNames = new String[size];
        int k = 0;
        for (int i = 0; i < size; i++) {
            if (isAny[i]) constantNames[i] = "c" + ++k;
        }

        StringBuilder sb = new StringBuilder("Infinite solutions:\n");
        for (int i = 0; i < size; i++) {
            if (isAny[i]) {
                sb.append( String.format("%s = %s", variableNames[i], constantNames[i]) ).append('\n');
                continue;
            }

            sb.append( String.format("%s = %s", variableNames[i], freeMembers[i]) );
            for (int j = 0; j < size; j++) {
                if (!isAny[j]) continue;

                Rational r = constants[i][j];
                if (r.equals(Rational.ZERO)) continue;

                if (r.absolute().equals(Rational.ONE)) {
                    sb.append( String.format(" %s %s", r.sign() == 1 ? "+" : "-", constantNames[j]) );
                    continue;
                }

                sb.append( String.format(" %s %s %s", r.sign() == 1 ? "+" : "-", r.absolute().toString(), constantNames[j]) );
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}
