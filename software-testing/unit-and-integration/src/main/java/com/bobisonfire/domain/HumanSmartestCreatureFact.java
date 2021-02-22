package com.bobisonfire.domain;

public class HumanSmartestCreatureFact implements Fact {
    @Override
    public boolean checkPointOfView(Creature c) {
        return c instanceof Human;
    }
}
