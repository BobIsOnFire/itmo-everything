package com.bobisonfire.foodshell.transformer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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
    public OffsetDateTime getDate(String key, String pattern) {
        try {
            return set.getTimestamp(key).toInstant().atOffset(ZoneOffset.UTC);
        } catch (SQLException e) {
            e.printStackTrace();
            return OffsetDateTime.now();
        }
    }
}
