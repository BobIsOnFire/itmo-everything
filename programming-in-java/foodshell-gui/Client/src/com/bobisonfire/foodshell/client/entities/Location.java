package com.bobisonfire.foodshell.client.entities;

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

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Location other) {
        return coordinate.compareTo(other.coordinate);
    }
}
