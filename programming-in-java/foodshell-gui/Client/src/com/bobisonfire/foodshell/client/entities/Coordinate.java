package com.bobisonfire.foodshell.client.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class Coordinate implements Comparable<Coordinate>, Serializable {
    private double x;
    private double y;
    private double z;

    public Coordinate(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    static Coordinate from(ResultSet set) throws SQLException {
        double x = set.getDouble("x");
        double y = set.getDouble("y");
        double z = set.getDouble("z");

        return new Coordinate(x, y, z);
    }

    public static Coordinate from(String string) {
        String[] tokens = string
                .substring(1, string.length() - 1)
                .split(",\\s*");
        double x = Double.parseDouble(tokens[0]);
        double y = Double.parseDouble(tokens[1]);
        double z = Double.parseDouble(tokens[2]);

        return new Coordinate(x, y, z);
    }

    public Coordinate getRelative(Coordinate other) {
        return new Coordinate(other.x - x, other.y - y, other.z - z);
    }

    public Coordinate getAbsolute(Coordinate other) {
        return new Coordinate(other.x + x, other.y + y, other.z + z);
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "(%.1f, %.1f, %.1f)", x, y, z);
    }

    @Override
    public int compareTo(Coordinate other) {
        return (int) (y - other.y);
    }
}
