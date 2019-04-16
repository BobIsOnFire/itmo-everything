package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.FileIOHelper;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.util.Iterator;
import java.util.Locale;

/**
 * Класс, реализующий локации - места расположения персонажей в <i>FoodShell</i>.<br>
 * Все локации хранятся в CSV-файле (путь к которому указывает сам пользователь), равно как
 * и в структуре TreeMap внутри локации, доступ к которой осуществляется методами
 * getLocationByName, getMap, setMap и update.
 * Локация World - основная локация, в нее помещаются все персонажи при отстутствии других локаций
 * и ее невозможно удалить.
 */
public class Location implements Comparable<Location>, CSVSerializable {
    public static final String CSV_HEAD = "name,x,y,z";
    public static String PATH = "location.csv";

    public static Location getLocationByName(String name) {
        Iterator<ObjectTransformer> iter = new FileIOHelper()
                        .readCSVSetFromFile(PATH)
                        .stream()
                        .filter(e -> e.getString("name").equals(name))
                        .iterator();

        if (iter.hasNext())
            return new Location(iter.next());

        return new Location();
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



    public String getName() {
        return name;
    }

    public Coordinate getCoords() {
        return coords;
    }

    @Override
    public int compareTo(Location other) {
        return coords.compareTo(other.coords);
    }

    @Override
    public String toString() {
        return String.format("%s:\t%s", name, coords.toString());
    }
}
