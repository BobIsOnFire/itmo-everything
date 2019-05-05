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
            ServerMain.logException(exc);
        }
    }

    public ResultSet getQuery(String sql, Object... preps) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            fillStatement(statement, preps);
            return statement.executeQuery();
        } catch (SQLException exc) {
            System.out.println("Cannot query from database.");
            ServerMain.logException(exc);
            return null;
        }
    }

    public int update(String sql, Object... preps) {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            fillStatement(statement, preps);
            return statement.executeUpdate();
        } catch (SQLException exc) {
            System.out.println("Cannot insert into database.");
            ServerMain.logException(exc);
            return 0;
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exc) {
            System.out.println("Caught exception during work with database.");
            ServerMain.logException(exc);
        }
    }

    private void fillStatement(PreparedStatement statement, Object[] preps) throws SQLException {
        for (int i = 0; i < preps.length; i++) {
            if (preps[i] instanceof Integer) {
                statement.setInt(i + 1, (Integer) preps[i]);
                continue;
            }
            if (preps[i] instanceof Double) {
                statement.setDouble(i + 1, (Double) preps[i]);
                continue;
            }
            statement.setString(i + 1, preps[i].toString());
        }
    }
}
