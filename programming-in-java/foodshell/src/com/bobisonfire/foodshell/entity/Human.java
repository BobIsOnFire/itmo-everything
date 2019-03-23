package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.FileIOHelper;
import com.bobisonfire.foodshell.exc.HumanNotFoundException;
import com.bobisonfire.foodshell.exc.LocationNotFoundException;
import com.bobisonfire.foodshell.transformer.CSVObject;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

public class Human implements CSVSerializable {
    public static final String CSV_HEAD = "name,sadness,birthday,maxSaturation,gender,location";
    public static String PATH = "human.csv";

    private static TreeMap<String, Human> HumanMap = new TreeMap<>();

    public static Human getHumanByName(String name) {
        if (!HumanMap.containsKey(name.intern()) && !name.intern().equals(""))
            throw new HumanNotFoundException(name);
        return HumanMap.get(name);
    }

    // Запускать каждый раз, когда изменяется состояние объектов
    public static void update() {
        mFileIOHelper.writeCSVMapIntoFile(HumanMap, false);
    }

    public String toCSV() {
        return String.format("%s,%d,%s,%d,%d,%s",
                name, sadness, getBirthday(), maxSaturation, gender.ordinal(), getLocation().getName());
    }

    public String getPath() {
        return PATH;
    }

    public String getCSVHead() {
        return CSV_HEAD;
    }



    private int sadness;
    private String name;
    private Date birthday;
    private int maxSaturation;
    private Gender gender;
    private Location location;

    private static FileIOHelper mFileIOHelper = new FileIOHelper();

    public int getSadness() {
        return sadness;
    }

    public void setSadness(int sadness) {
        this.sadness = sadness;
    }

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

    public int getAge() {
        long ms = new Date().getTime() - birthday.getTime();
        long mod = (long) 1000 * 60 * 60 * 24 * 365;
        return (int) ( ms / mod );
    }

    public int getMaxSaturation() {
        return maxSaturation;
    }

    public int getCurrentSaturation() {
        ArrayList<Food> list = readMeals();

        int saturation = 0;
        for (Food food: list) {
            if (food.isAffecting())
                saturation += food.getSaturation();
        }
        return saturation;
    }

    public Gender getGender() {
        return gender;
    }

    public Location getLocation() {
        try {
            Location.getLocationByName(location.getName());
        }
        catch(LocationNotFoundException exc) {
            location = Location.getLocationByName("World");
        }
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



    public Human(ObjectTransformer objectTransformer, boolean serialize) {
        name = objectTransformer.getString("name");
        birthday = objectTransformer.getDate("birthday", "dd.MM.yyyy");
        maxSaturation = objectTransformer.getInt("maxSaturation");
        sadness = objectTransformer.getInt("sadness");

        gender = Gender.getGenderByNumber( objectTransformer.getInt("gender") );
        location = Location.getLocationByName( objectTransformer.getString("location") );

        HumanMap.put(name, this);

        if (serialize)
            mFileIOHelper.writeCSVMapIntoFile(HumanMap, true);
    }

    public Human() {
        name = "God";
        birthday = new Date(1);
        maxSaturation = 1000;
        sadness = 0;
        gender = Gender.getGenderByNumber(2);
        location = Location.getLocationByName("World");

        HumanMap.put(name, this);
        mFileIOHelper.writeCSVMapIntoFile(HumanMap, false);
    }

    public Human(String name, Date birthday, int maxSaturation, Gender gender) {
        this.name = name;
        this.birthday = birthday;
        this.maxSaturation = maxSaturation;
        this.gender = gender;
        this.location = Location.getLocationByName("World");
        this.sadness = 0;

        HumanMap.put(name, this);
        mFileIOHelper.writeCSVMapIntoFile(HumanMap, false);
    }



    public ArrayList<Food> readMeals() {
        ArrayList<Food> list = new ArrayList<>();
        ArrayList<CSVObject> map = mFileIOHelper.readCSVListFromFile(Food.PATH);

        for (CSVObject csv: map) {
            if (csv.getString("consumer").equals(name))
                list.add( new Food( csv ) );
        }

        return list;
    }

    public void sayPhrase(String phrase) {
        System.out.println("\t" + name + ": " + phrase);
    }
}
