package com.bobisonfire.foodshell.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Location implements Comparable<Location>, Serializable {
    private Coordinate coordinate;
    private int size;
    private String name;
    private int id;

    public static Location from(ResultSet set) throws SQLException {
        Location location = new Location();

        location.coordinate = Coordinate.from(set);
        location.size = set.getInt("size");
        location.name = set.getString("name");
        location.id = set.getInt("id");

        return location;
    }

    @Override
    public int compareTo(Location other) {
        return coordinate.compareTo(other.coordinate);
    }
}
