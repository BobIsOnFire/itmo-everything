package com.bobisonfire.domain;

public class HumanBuiltNewYorkFact implements Fact {
    @Override
    public boolean checkPointOfView(Creature c) {
        return true;
    }
}
