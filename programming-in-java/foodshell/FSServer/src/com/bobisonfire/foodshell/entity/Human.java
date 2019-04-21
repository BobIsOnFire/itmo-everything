package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.FileIOHelper;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Класс, реализующий персонажей - основных действующих единиц <i>FoodShell</i>.<br>
 * Все персонажи хранятся в CSV-файле (путь к которому указывает сам пользователь).<br>
 * Персонаж God - основной персонаж, с ним запускается <i>FoodShell</i> и он "создает"
 * других персонажей. Его невозможно удалить.
 */
public class Human implements Comparable<Human>, CSVSerializable {
    public static final String CSV_HEAD = "name,birthday,gender,location,creationDate";
    public static String PATH = "human.csv";

    public static Human getHumanByName(String name, String path) {
        Iterator<ObjectTransformer> iter = new FileIOHelper()
                .readCSVSetFromFile(path)
                .stream()
                .filter(e -> e.getString("name").equals(name))
                .iterator();

        if (iter.hasNext())
            return new Human(iter.next());

        return new Human();
    }

    public String toCSV() {
        return String.format("%s,%s,%d,%s,%s",
                name, getBirthday(), gender.ordinal(), getLocation().getName(), getCreationDate());
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
    private Location location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
        location = Location.getLocationByName("World");
        creationDate = new Date();
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
                gender.getName(), name, getAge(), location.getName(), getCreationDate());
    }
}
