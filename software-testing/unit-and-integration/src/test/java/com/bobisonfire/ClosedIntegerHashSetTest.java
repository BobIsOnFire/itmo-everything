package com.bobisonfire;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClosedIntegerHashSetTest {
    private ClosedIntegerHashSet set;

    @BeforeEach
    void setUp() {
        set = new ClosedIntegerHashSet();
    }

    @Test
    public void test_empty_set() {
        assertTrue(set.isEmpty());
    }

    @Test
    public void test_add_elements() {
        assertTrue(set.add(2));
        assertEquals(1, set.size());

        assertTrue(set.add(3));
        assertEquals(2, set.size());
    }

    @Test
    public void test_contains() {
        assertTrue(set.add(2));
        assertTrue(set.contains(2));

        assertFalse(set.contains(1));
        assertFalse(set.contains(18));
    }

    @Test
    public void test_add_same_elements() {
        assertTrue(set.add(2));
        assertFalse(set.add(2));

        assertEquals(1, set.size());
        assertTrue(set.contains(2));
    }

    @Test
    public void test_remove() {
        assertTrue(set.add(2));
        assertFalse(set.add(2));
        assertTrue(set.add(3));

        assertTrue(set.contains(2));

        assertTrue(set.remove(2));
        assertFalse(set.contains(2));
        assertEquals(1, set.size());

        assertFalse(set.remove(2));
        assertEquals(1, set.size());

        assertTrue(set.remove(3));
        assertTrue(set.isEmpty());
    }

    @Test
    public void test_clear() {
        assertTrue(set.add(2));
        assertTrue(set.add(3));
        assertFalse(set.isEmpty());

        set.clear();
        assertTrue(set.isEmpty());
    }
}
