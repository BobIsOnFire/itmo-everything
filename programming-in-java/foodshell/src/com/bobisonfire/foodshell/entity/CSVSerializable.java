package com.bobisonfire.foodshell.entity;

import java.io.Serializable;

public interface CSVSerializable extends Serializable {
    String getPath();
    String getCSVHead();
    String toCSV();
}
