package com.bobisonfire.foodshell.entity;

import java.io.Serializable;

/**
 * Интерфейс, реализующий поведение объектов для их более простой сериализации
 * и десериализации в формате CSV.
 */
public interface CSVSerializable extends Serializable {
    String getPath();
    String getCSVHead();
    String toCSV();
}
