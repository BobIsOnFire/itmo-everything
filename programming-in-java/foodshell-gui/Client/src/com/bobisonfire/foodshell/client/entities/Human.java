package com.bobisonfire.foodshell.client.entities;

import java.io.Serializable;

public class Human implements Comparable<Human>, Serializable {
    private String name;
    private int id;

    private int creatorID;
    private int locationID;

    private String birthday;
    private Gender gender;
    private String creationDate;
    private Coordinate coordinate;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getCreatorID() {
        return creatorID;
    }

    public int getLocationID() {
        return locationID;
    }

    public String getBirthday() {
        return birthday;
    }

    public Gender getGender() {
        return gender;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCreatorID(int creatorID) {
        this.creatorID = creatorID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public int compareTo(Human o) {
        return birthday.compareTo(o.birthday);
    }
}
