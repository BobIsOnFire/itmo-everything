package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.YEARS;

/**
 * Класс, реализующий персонажей - основных действующих единиц <i>FoodShell</i>.<br>
 * Все персонажи хранятся в базе данных.<br>
 * Персонаж God - основной персонаж, чье состояние не может изменить ни один пользователь.
 */
public class Human implements Comparable<Human> {
    private String name;
    private ZonedDateTime birthday;
    private Gender gender;
    private String location;
    private ZonedDateTime creationDate;

    public String getName() {
        return name;
    }

    public String getBirthday(String pattern) {
        return birthday.format( DateTimeFormatter.ofPattern(pattern) );
    }

    public String getBirthday() {
        return this.getBirthday("dd.MM.yyyy");
    }

    private String getCreationDate() {
        return creationDate.format( DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss") );
    }

    private int getAge() {
        return (int) YEARS.between(birthday, OffsetDateTime.now());
    }

    public Gender getGender() {
        return gender;
    }

    public String getLocation() {
        return location;
    }



    public Human(ObjectTransformer objectTransformer) {
        name = objectTransformer.getString("name");
        birthday = objectTransformer.getDate("birthday", "dd.MM.yyyy");
        gender = Gender.getGenderByNumber( objectTransformer.getInt("gender") );
        location = Location.getLocationByName( objectTransformer.getString("location") );
        creationDate = objectTransformer.getDate("creation_date", "dd.MM.yyyy HH:mm:ss");
    }

    public Human() {
        name = "God";
        birthday = ZonedDateTime.parse("1970-01-01T00:00:00+00:00");
        gender = Gender.getGenderByNumber(2);
        location = "World";
        creationDate = ZonedDateTime.now();
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
