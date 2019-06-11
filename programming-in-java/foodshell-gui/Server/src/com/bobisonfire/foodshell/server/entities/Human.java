package com.bobisonfire.foodshell.server.entities;

import java.io.Serializable;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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

    public String getCreationDate() {
        return creationDate;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public static Human from(ResultSet set) throws SQLException {
        Human human = new Human();

        human.id = set.getInt("id");
        human.name = set.getString("name");

        human.birthday = set.getTimestamp("birthday").toInstant().atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        human.gender = Gender.getGenderByNumber( set.getInt("gender") );

        human.creatorID = set.getInt("creator_id");
        human.locationID = set.getInt("location_id");

        human.coordinate = Coordinate.from(set);
        human.creationDate = set.getTimestamp("creation_date").toInstant().atZone(ZoneId.systemDefault()).toString();

        return human;
    }

    public Date getBirthdayAsDate() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        try {
            return new Date( format.parse(birthday).getTime() );
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date(0);
        }
    }

    public Timestamp getCreationDateAsTimestamp() {
        ZonedDateTime time = ZonedDateTime.parse(creationDate);
        return new Timestamp( time.toInstant().toEpochMilli() );
    }

    @Override
    public int compareTo(Human o) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            java.util.Date self = format.parse(birthday);
            java.util.Date other = format.parse(o.birthday);
            return self.compareTo(other);
        } catch (ParseException exc) {
            return birthday.compareTo(o.birthday);
        }
    }
}
