package com.bobisonfire.lab4.jmx;

import com.bobisonfire.lab4.data.HistoryEntity;

public interface PointCounterMBean {
    int getShootCounter();
    int getHitCounter();
    void addNode(HistoryEntity entity);
}
