package com.bobisonfire.domain;

public class HumanInventedWheelFact implements Fact {
    @Override
    public boolean checkPointOfView(Creature c) {
        return true;
    }
}
