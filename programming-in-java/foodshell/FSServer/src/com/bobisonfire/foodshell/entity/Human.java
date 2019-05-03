package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.transformer.CSVObject;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Класс, реализующий персонажей - основных действующих единиц <i>FoodShell</i>.<br>
 * Все персонажи хранятся в CSV-файле (путь к которому указывает сам пользователь).<br>
 * Персонаж God - основной персонаж, с ним запускается <i>FoodShell</i> и он "создает"
 * других персонажей. Его невозможно удалить.
 */
public class Human implements Comparable<Human>, CSVSerializable {
    public static final String CSV_HEAD = "name,birthday,gender,location,creationDate";
    public static String PATH = "human.csv";

    public String toCSV() {
        return String.format("%s,%s,%d,%s,%s",
                name, getBirthday(), gender.ordinal(), location, getCreationDate());
    }

    public String getPath() {
        return PATH;
    }

    public String getCSVHead() {
        return CSV_HEAD;
    }



    private String name;
    private Date birthday;
    private Gender gender;
    private String location;
    private Date creationDate;

    public String getName() {
        return name;
    }

    public String getBirthday(String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(birthday);
    }

    public String getBirthday() {
        return this.getBirthday("dd.MM.yyyy");
    }

    public String getCreationDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(creationDate);
    }

    public void setCreationDate(Date date) {
        creationDate = date;
    }

    public int getAge() {
        long ms = new Date().getTime() - birthday.getTime();
        long mod = (long) 1000 * 60 * 60 * 24 * 365;
        return (int) ( ms / mod );
    }

    public Gender getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location.getName();
    }



    public Human(ObjectTransformer objectTransformer) {
        name = objectTransformer.getString("name");
        birthday = objectTransformer.getDate("birthday", "dd.MM.yyyy");
        gender = Gender.getGenderByNumber( objectTransformer.getInt("gender") );
        location = Location.getLocationByName( objectTransformer.getString("location") );
        creationDate = objectTransformer.getDate("creationDate", "dd.MM.yyyy HH:mm:ss");
    }

    public Human() {
        name = "God";
        birthday = new Date(1);
        gender = Gender.getGenderByNumber(2);
        location = "World";
        creationDate = new Date();
    }

    public Human(ResultSet set) throws SQLException {
        name = set.getString("name");
        birthday = set.getDate("birthday");
        gender = Gender.getGenderByNumber( set.getInt("gender") );
        location = Location.getLocationByName( set.getString("location") );
        creationDate = set.getTimestamp("creation_date"); // todo по заданию хранится в другом формате
    }

    public int compareTo(Human other) {
        return birthday.compareTo(other.birthday);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Human)
            return name.equals(((Human) obj).name);
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s %s, %d лет, находится в %s; создан %s",
                gender.getName(), name, getAge(), location, getCreationDate());
    }
}
