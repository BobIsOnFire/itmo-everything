package com.bobisonfire;

import com.bobisonfire.domain.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DomainTest {
    private static Creature human;
    private static Creature dolphin;

    private static Fact swimFact;
    private static Fact nyFact;
    private static Fact wheelFact;
    private static Fact smartFact;
    private static Fact truthFact;

    @BeforeAll
    static void setUp() {
        human = new Human();
        dolphin = new Dolphin();

        swimFact = new DolphinOnlySwimFact();
        nyFact = new HumanBuiltNewYorkFact();
        wheelFact = new HumanInventedWheelFact();
        smartFact = new HumanSmartestCreatureFact();
        truthFact = new TruthSameForEveryoneFact();
    }

    @Test
    public void test_common_fact() {
        assertFalse(truthFact.checkPointOfView(human));
        assertFalse(truthFact.checkPointOfView(dolphin));
    }

    @Test
    public void test_human_created_things() {
        assertTrue(nyFact.checkPointOfView(human));
        assertTrue(wheelFact.checkPointOfView(human));
    }

    @Test
    public void test_human_humiliates_dolphins() {
        assertTrue(swimFact.checkPointOfView(human));
    }

    @Test
    public void test_dolphin_knows_human_created_things() {
        assertTrue(nyFact.checkPointOfView(dolphin));
        assertTrue(wheelFact.checkPointOfView(dolphin));
    }

    @Test
    public void test_dolphin_hides_something() {
        assertFalse(swimFact.checkPointOfView(dolphin));
    }

    @Test
    public void test_smartest_creature_unknown() {
        assertTrue(smartFact.checkPointOfView(human));
        assertFalse(smartFact.checkPointOfView(dolphin));
    }
}
