package com.bobisonfire.test;

import com.bobisonfire.gauss.matrix.Rational;
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
        assertEquals( Rational.from(1, 5).multiply( Rational.from(2, 7) ), Rational.from(2, 35));
        assertEquals( Rational.from(2, 3).multiply( Rational.from(3, 5) ), Rational.from(2, 5));
        assertEquals( Rational.ZERO.multiply( Rational.from(213, 543) ), Rational.ZERO);
    }

    @Test
    void divide() {
        assertEquals( Rational.from(2, 3).divide( Rational.from(2, 5) ), Rational.from(5, 3));
        assertThrows(RuntimeException.class, () -> Rational.ONE.divide(Rational.ZERO));
    }
}