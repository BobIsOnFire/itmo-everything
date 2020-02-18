package com.bobisonfire.gauss.solution;

import com.bobisonfire.gauss.matrix.Rational;

public class Solution {
    private boolean infiniteSolutions;
    private boolean noSolutions;

    private boolean[] isAny;
    private Rational[] freeMembers;
    private Rational[][] constants;

    public boolean isInfiniteSolutions() {
        return infiniteSolutions;
    }

    public boolean isNoSolutions() {
        return noSolutions;
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

    @Override
    public String toString() {
        if (noSolutions) return "No solutions";

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < freeMembers.length; i++) {
            String s = String.format("x%d = %s", i + 1, freeMembers[i]);

            if (!infiniteSolutions) {
                sb.append(s).append('\n');
                continue;
            }

            if (isAny[i]) {
                sb.append( String.format("x%d = x%d", i + 1, i + 1) ).append('\n');
                continue;
            }

            sb.append(s);
            for (int j = 0; j < isAny.length; j++) {
                if (!isAny[j]) continue;

                Rational r = constants[i][j];
                if (r.equals(Rational.ZERO)) continue;

                sb.append( String.format(" %s %s x%d", r.sign() == 1 ? "+" : "-", r.absolute().toString(), j + 1) );
            }
            sb.append('\n');
        }

        return sb.toString();
    }
}
