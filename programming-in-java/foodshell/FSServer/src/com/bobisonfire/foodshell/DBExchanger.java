package com.bobisonfire.foodshell;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DBExchanger implements AutoCloseable {
    private final static String PROPERTIES_PATH = "database.properties";
    private Connection connection;

    public DBExchanger() {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(PROPERTIES_PATH))) {
            props.load(in);
        } catch (IOException exc) {
            System.out.println("Cannot read properties.");
        }

        System.setProperty("jdbc.drivers", props.getProperty("jdbc.drivers"));
        try {
            connection = DriverManager.getConnection(
                    props.getProperty("jdbc.url"),
                    props.getProperty("jdbc.username"),
                    props.getProperty("jdbc.password")
            );
        } catch (SQLException exc) {
            System.out.println("Cannot connect to database.");
            exc.printStackTrace();
        }
    }

    public ResultSet getQuery(String sql) {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException exc) {
            System.out.println("Cannot query from database.");
            exc.printStackTrace();
            return null;
        }
    }

    public void update(String sql) {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException exc) {
            System.out.println("Cannot insert into database.");
            exc.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exc) {
            System.out.println("Caught exception during work with database.");
            exc.printStackTrace();
        }
    }
}
