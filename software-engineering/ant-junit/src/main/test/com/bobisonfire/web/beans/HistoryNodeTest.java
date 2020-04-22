package com.bobisonfire.web.beans;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class HistoryNodeTest {
    private HistoryNode node;

    @Before
    public void init() {
        node = new HistoryNode();
        node.setR(BigDecimal.ONE);
    }

    @Test
    public void test_node_null_parameters() {
        HistoryNode nullNode = new HistoryNode();
        assertFalse(nullNode.isHit());
    }

    @Test
    public void test_node_top_left() {
        node.setX(BigDecimal.ZERO);
        node.setY(BigDecimal.ZERO);
        assertTrue(node.isHit());

        node.setX(BigDecimal.ONE.negate());
        node.setY(BigDecimal.ONE);
        assertFalse(node.isHit());

        node.setX(new BigDecimal("-0.000000000000000000001"));
        node.setY(new BigDecimal("0.00000000000000000001"));
        assertFalse(node.isHit());

        node.setX(BigDecimal.valueOf(-0.5d));
        node.setY(BigDecimal.valueOf(0.5d));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_top_right() {
        node.setX(BigDecimal.ONE);
        node.setY(BigDecimal.ZERO);
        assertTrue(node.isHit());

        node.setX(BigDecimal.ZERO);
        node.setY(BigDecimal.ONE);
        assertTrue(node.isHit());

        node.setX(BigDecimal.valueOf(0.6d));
        node.setY(BigDecimal.valueOf(0.8d));
        assertTrue(node.isHit());

        node.setX(new BigDecimal("0.60000000000000000000000001"));
        node.setY(BigDecimal.valueOf(0.8d));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_bottom_left() {
        node.setX(BigDecimal.valueOf(-0.25d));
        node.setY(BigDecimal.valueOf(-0.5d));
        assertTrue(node.isHit());

        node.setX(BigDecimal.valueOf(-0.5d));
        node.setY(BigDecimal.ONE.negate());
        assertTrue(node.isHit());

        node.setX(BigDecimal.ZERO);
        node.setY(new BigDecimal("-1.00000000000000000001"));
        assertFalse(node.isHit());

        node.setX(BigDecimal.valueOf(-0.5d));
        node.setY(new BigDecimal("0.00000000000000000001"));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_bottom_right() {
        node.setX(BigDecimal.valueOf(0.1d));
        node.setY(BigDecimal.valueOf(-0.1d));
        assertTrue(node.isHit());

        node.setX(BigDecimal.valueOf(0.5d));
        node.setY(BigDecimal.valueOf(-0.1d));
        assertFalse(node.isHit());

        node.setX(BigDecimal.valueOf(0.25d));
        node.setY(BigDecimal.valueOf(-0.25d));
        assertTrue(node.isHit());

        node.setX(BigDecimal.valueOf(0.25d));
        node.setY(new BigDecimal("-0.2500000000000000000001"));
        assertFalse(node.isHit());
    }
}
