package com.bobisonfire.domain;

public class DolphinOnlySwimFact implements Fact {
    @Override
    public boolean checkPointOfView(Creature c) {
        return ! (c instanceof Dolphin);
    }
}
