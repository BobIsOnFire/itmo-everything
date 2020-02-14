package com.bobisonfire.test;

import com.bobisonfire.gauss.Rational;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RationalTest {

    @Test
    void from() {
        assertEquals(Rational.from(3).toInt(), 3);
        assertEquals(Rational.from(6, 2).toInt(), 3);
        assertEquals(Rational.from(5, 2).toDouble(), 2.5);

        assertThrows(RuntimeException.class, () -> Rational.from(1, 0));

        assertEquals(Rational.from(0, 5), Rational.ZERO);
        assertEquals(Rational.from(360, 540), Rational.from(2, 3));
    }

    @Test
    void add() {
        assertEquals( Rational.from(2, 5).add( Rational.from(1, 5) ), Rational.from(3, 5) );
        assertEquals( Rational.from(2, 5).add( Rational.from(3, 5) ), Rational.ONE );
    }

    @Test
    void multiply() {
    }

    @Test
    void subtract() {
    }

    @Test
    void divide() {
    }
}