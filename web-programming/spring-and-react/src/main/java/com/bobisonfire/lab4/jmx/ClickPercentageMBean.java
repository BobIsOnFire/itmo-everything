package com.bobisonfire.lab4.jmx;

import com.bobisonfire.lab4.data.HistoryEntity;

public interface ClickPercentageMBean {
    double getPercentage();
    void addNode(HistoryEntity entity);
}
