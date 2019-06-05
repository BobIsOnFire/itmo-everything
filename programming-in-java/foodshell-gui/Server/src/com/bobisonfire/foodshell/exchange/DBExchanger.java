package com.bobisonfire.foodshell.exchange;

import com.bobisonfire.foodshell.ServerException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

public class DBExchanger implements AutoCloseable {
    private final static String PROPERTIES_PATH = "database.properties";
    private static String DB_LOGIN = "";
    private static String DB_PASSWORD = "";
    private Connection connection;

    public static void setInitials(String login, String password) {
        DB_LOGIN = login;
        DB_PASSWORD = password;
    }

    public DBExchanger() throws ServerException {
        this(DB_LOGIN, DB_PASSWORD);
    }

    private DBExchanger(String login, String password) throws ServerException {
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(Paths.get(PROPERTIES_PATH))) {
            props.load(in);
        } catch (IOException exc) {
            throw new ServerException("Произошла ошибка чтения свойств БД.", exc);
        }

        System.setProperty("jdbc.drivers", props.getProperty("jdbc.drivers"));
        try {
            connection = DriverManager.getConnection(
                    props.getProperty("jdbc.url"),
                    login,
                    password
            );
        } catch (SQLException exc) {
            throw new ServerException("Невозможно подключиться к БД", exc);
        }
    }

    public ResultSet getQuery(String sql, Object... preps) throws ServerException {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            fillStatement(statement, preps);
            return statement.executeQuery();
        } catch (SQLException exc) {
            throw new ServerException("Невозможно создать запрос в БД", exc);
        }
    }

    public int update(String sql, Object... preps) throws ServerException {
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            fillStatement(statement, preps);
            return statement.executeUpdate();
        } catch (SQLException exc) {
            throw new ServerException("Невозможно изменить БД", exc);
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException exc) {
            System.out.println("Caught exception during work with database.");
            // todo logging but different
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
            if (preps[i] instanceof Boolean) {
                statement.setBoolean(i + 1, (Boolean) preps[i]);
            }
            statement.setString(i + 1, preps[i].toString());
        }
    }
}