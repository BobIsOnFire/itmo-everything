package com.bobisonfire.foodshell.server.entities;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User implements Comparable<User>, Serializable {
    private int id;
    private String email;
    private String password;
    private String name;
    private int color;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public static User from(ResultSet set) throws SQLException {
        User user = new User();

        user.id = set.getInt("id");
        user.email = set.getString("email");
        user.password = set.getString("password");
        user.name = set.getString("name");
        user.color = set.getInt("color");

        return user;
    }

    @Override
    public int compareTo(User other) {
        return id - other.id;
    }
}
