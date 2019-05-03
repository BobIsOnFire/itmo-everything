package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.DBExchanger;
import com.bobisonfire.foodshell.transformer.CSVObject;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * Класс, реализующий локации - места расположения персонажей в <i>FoodShell</i>.<br>
 * Локация World - основная локация, в нее помещаются все персонажи при отстутствии других локаций
 * и ее невозможно удалить.
 */
public class Location implements Comparable<Location>, CSVSerializable {
    public static final String CSV_HEAD = "name,x,y,z";
    public static String PATH = "location.csv";

    public static String getLocationByName(String name) {
        try (DBExchanger exchanger = new DBExchanger()) {
            ResultSet set = exchanger.getQuery("SELECT name FROM locations WHERE name LIKE '" + name + "';");
            if (set.next())
                return set.getString("name");
            else
                return "World";
        } catch (SQLException exc) {
            System.out.println("Произошла ошибка при чтении базы.");
            exc.printStackTrace();
            return "World";
        }
    }

    public String toCSV() {
        return String.format(Locale.US, "%s,%.3f,%.3f,%.3f",
                name, coords.getX(), coords.getY(), coords.getZ());
    }

    public String getPath() {
        return PATH;
    }

    public String getCSVHead() {
        return CSV_HEAD;
    }



    private String name;
    private Coordinate coords;

    public Location(ObjectTransformer objectTransformer) {
        this.name = objectTransformer.getString("name");
        this.coords = new Coordinate(
                objectTransformer.getDouble("x"),
                objectTransformer.getDouble("y"),
                objectTransformer.getDouble("z")
        );
    }

    public Location() {
        name = "World";
        coords = new Coordinate(0, 0, 0);
    }

    public Location(String name, Coordinate coords) {
        this.name = name;
        this.coords = coords;
    }

    public Location(ResultSet set) throws SQLException {
        this.name = set.getString("name");
        this.coords = new Coordinate(
                set.getDouble("x"),
                set.getDouble("y"),
                set.getDouble("z")
        );
    }



    public String getName() {
        return name;
    }

    public Coordinate getCoords() {
        return coords;
    }

    @Override
    public int compareTo(Location other) {
        return name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return String.format("%s:\t%s", name, coords.toString());
    }
}
