package com.bobisonfire.foodshell.transformer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Класс-оболочка для java.sql.ResultSet, позволяющий использовать его содержимое как ObjectTransformer.<br>
 * Гарантируется, что методы этого класса не изменяют положение указателя в ResultSet и только считывают данные
 * по столбцам текущей строки в БД.
 */
public class SQLObject extends ObjectTransformer {
    private ResultSet set;
    public SQLObject(ResultSet set) {
        this.set = set;
    }

    @Override
    public double getDouble(String key) {
        try {
            return set.getDouble(key);
        } catch (SQLException e) {
            return 0.0D;
        }
    }

    @Override
    public int getInt(String key) {
        try {
            return set.getInt(key);
        } catch (SQLException e) {
            return 0;
        }
    }

    @Override
    public String getString(String key) {
        try {
            return set.getString(key);
        } catch (SQLException e) {
            return "";
        }
    }

    @Override
    public ZonedDateTime getDate(String key, String pattern) {
        try {
            return set.getTimestamp(key).toInstant().atZone(ZoneId.systemDefault());
        } catch (SQLException e) {
            e.printStackTrace();
            return ZonedDateTime.now();
        }
    }
}
