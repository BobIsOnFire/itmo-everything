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
    public void test_node_zero() {
        node.setX(BigDecimal.ZERO);
        node.setY(BigDecimal.ZERO);
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_far_top_left() {
        node.setX(BigDecimal.ONE.negate());
        node.setY(BigDecimal.ONE);
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_top_left_near_zero() {
        node.setX(new BigDecimal("-0.000000000000000000001"));
        node.setY(new BigDecimal("0.00000000000000000001"));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_far_right() {
        node.setX(BigDecimal.ONE);
        node.setY(BigDecimal.ZERO);
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_circle_in_bound() {
        node.setX(BigDecimal.valueOf(0.5d));
        node.setY(BigDecimal.valueOf(0.5d));
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_circle_on_bound() {
        node.setX(BigDecimal.valueOf(0.6d));
        node.setY(BigDecimal.valueOf(0.8d));
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_circle_slightly_off_bound() {
        node.setX(new BigDecimal("0.60000000000000000000000001"));
        node.setY(BigDecimal.valueOf(0.8d));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_rectangle_in_bound() {
        node.setX(BigDecimal.valueOf(-0.25d));
        node.setY(BigDecimal.valueOf(-0.5d));
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_rectangle_on_bound() {
        node.setX(BigDecimal.valueOf(-0.5d));
        node.setY(BigDecimal.ONE.negate());
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_rectangle_slightly_off_bound() {
        node.setX(BigDecimal.ZERO);
        node.setY(new BigDecimal("-1.00000000000000000001"));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_rectangle_off_bound() {
        node.setX(BigDecimal.ONE.negate());
        node.setY(BigDecimal.ONE.negate());
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_triangle_in_bound() {
        node.setX(BigDecimal.valueOf(0.1d));
        node.setY(BigDecimal.valueOf(-0.1d));
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_triangle_off_bound() {
        node.setX(BigDecimal.valueOf(0.5d));
        node.setY(BigDecimal.valueOf(-0.1d));
        assertFalse(node.isHit());
    }

    @Test
    public void test_node_triangle_on_bound() {
        node.setX(BigDecimal.valueOf(0.25d));
        node.setY(BigDecimal.valueOf(-0.25d));
        assertTrue(node.isHit());
    }

    @Test
    public void test_node_triangle_slightly_off_bound() {
        node.setX(BigDecimal.valueOf(0.25d));
        node.setY(new BigDecimal("-0.2500000000000000000001"));
        assertFalse(node.isHit());
    }
}
