package com.bobisonfire.foodshell.transformer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

/**
 * Класс, реализующий десериализацию объектов.<br>
 * Поля и значения хранятся в соответствующей структуре values.
 */
public abstract class ObjectTransformer {
    protected TreeMap<String, String> values = new TreeMap<>();
    protected String value;

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

    public Date getDate(String key, String pattern) {
        if (!values.containsKey(key))
            return new Date();

        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date = new Date();

        try {
            date = sdf.parse(values.get(key));
        }
        catch(ParseException exc) {
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
