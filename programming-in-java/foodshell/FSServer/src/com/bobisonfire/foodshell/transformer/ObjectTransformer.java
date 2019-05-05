package com.bobisonfire.foodshell.transformer;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.TreeMap;

/**
 * Класс, реализующий десериализацию объектов.<br>
 * Поля и значения хранятся в соответствующей структуре values.
 */
public abstract class ObjectTransformer {
    TreeMap<String, String> values = new TreeMap<>();
    String value;

    public String getString(String key) {
        if (!values.containsKey(key))
            return "";
        return values.get(key);
    }

    public int getInt(String key) {
        if (!values.containsKey(key))
            return 0;
        return Integer.parseInt(values.get(key));
    }

    public double getDouble(String key) {
        if (!values.containsKey(key))
            return 0.0D;
        return Double.parseDouble(values.get(key));
    }

    public long getLong(String key) {
        if (!values.containsKey(key))
            return 0L;
        return Long.parseLong(values.get(key));
    }

    public OffsetDateTime getDate(String key, String pattern) {
        if (!values.containsKey(key))
            return OffsetDateTime.now();
        OffsetDateTime date = OffsetDateTime.now();

        try {
            date = OffsetDateTime.of(
                    LocalDate.parse(values.get(key), DateTimeFormatter.ofPattern(pattern)).atTime(0, 0),
                    ZoneOffset.UTC
            );
            //date = OffsetDateTime.parse(values.get(key), DateTimeFormatter.ofPattern(pattern));
        }
        catch(DateTimeParseException exc) {
            System.out.println("Wrong pattern! Setting today's date..");
        }
        return date;
    }

    public void put(String key, String value) {
        values.put(key, value);
    }

    public String toString() {
        return value;
    }
}
