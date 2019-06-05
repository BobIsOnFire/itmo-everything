package com.bobisonfire.foodshell.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Human implements Comparable<Human>, Serializable {
    private String name;
    private int id;

    private int creatorID;
    private int locationID;

    private ZonedDateTime birthday;
    private Gender gender;
    private ZonedDateTime creationDate;
    private Coordinate coordinate;

    public static Human from(ResultSet set) throws SQLException {
        Human human = new Human();

        human.id = set.getInt("id");
        human.name = set.getString("name");
        human.birthday = set.getTimestamp("birthday").toInstant().atZone(ZoneId.systemDefault());
        human.gender = Gender.getGenderByNumber( set.getInt("gender") );

        human.creatorID = set.getInt("creator_id");
        human.locationID = set.getInt("location_id");

        human.coordinate = Coordinate.from(set);
        human.creationDate = set.getTimestamp("creation_date").toInstant().atZone(ZoneId.systemDefault());

        return human;
    }

    @Override
    public int compareTo(Human o) {
        return birthday.compareTo(o.birthday);
    }
}
