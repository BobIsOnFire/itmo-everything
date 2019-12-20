package com.bobisonfire.lab4;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "history")
public class HistoryNode {
    @Id @GeneratedValue
    private long id;

    @Column(precision = 25, scale = 20) private BigDecimal x;
    @Column(precision = 25, scale = 20) private BigDecimal y;
    @Column(precision = 25, scale = 20) private BigDecimal r;
    private int result;

    public HistoryNode() {
    }

    public HistoryNode(BigDecimal x, BigDecimal y, BigDecimal r, int result) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.result = result;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getX() {
        return x;
    }

    public void setX(BigDecimal x) {
        this.x = x;
    }

    public BigDecimal getY() {
        return y;
    }

    public void setY(BigDecimal y) {
        this.y = y;
    }

    public BigDecimal getR() {
        return r;
    }

    public void setR(BigDecimal r) {
        this.r = r;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
