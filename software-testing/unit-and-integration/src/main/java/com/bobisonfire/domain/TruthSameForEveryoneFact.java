package com.bobisonfire.domain;

public class TruthSameForEveryoneFact implements Fact {
    @Override
    public boolean checkPointOfView(Creature c) {
        return false;
    }
}
