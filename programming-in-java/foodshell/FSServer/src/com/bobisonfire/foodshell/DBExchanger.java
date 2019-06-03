package com.bobisonfire.foodshell;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Properties;

/**
 * Класс, инкапсулирующий взаимодействие с базой данных PostgreSQL, используя JDBC.
 * После обращения к БД рекомендуется закрыть соединение или обернуть все текущее
 * взаимодействие с БД в блок try-with-resources.<br>
 * Использует файл со свойствами (database.properties), который должен содержать
 * следующие свойства:<br>
 * 1. jdbc.drivers - путь к классу - драйверу данной БД. Библиотека с драйвером должна
 * существовать и быть прописанной в classpath или manifest-файле.<br>
 * 2. jdbc.url - URL к базе данных. Общий вид: jdbc:[протокол]://[хост]:[порт]/[имя базы]<br>
 * 3. В случае необходимости авторизации в БД:<br>
 * - jdbc.username - логин<br>
 * - jdbc.password - пароль
 */
public class DBExchanger implements AutoCloseable {
    private final static String PROPERTIES_PATH = "/home/s264443/prog/lab7/database.properties";
    private Connection connection;

    /**
     * Конструктор, устанавливающий соединение с БД по данным свойствам.
     */
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
                    ServerMain.dbLogin,
                    ServerMain.dbPassword
            );
        } catch (SQLException exc) {
            System.out.println("Cannot connect to database.");
            ServerMain.logException(exc);
        }
    }

    /**
     * Метод, осуществляющий запрос к данной БД.
     * @param sql - SQL-запрос (может быть prepared - в этом случае каждый пропуск заполняется знаком ?)
     * @param preps - объекты (числа, строки или логические значения), которые необходимо вставить на место знаков ?
     * @return результат запроса
     */
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

    /**
     * Метод, осуществляющий изменение БД.
     * @param sql - SQL-изменение (может быть prepared - в этом случае каждый пропуск заполняется знаком ?)
     * @param preps - объекты (числа, строки или логические значения), которые необходимо вставить на место знаков ?
     * @return количество измененных строк
     */
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

    /**
     * Закрытие соединения с БД и освобождение ресурсов. При закрытии соединения закрываются и все ResultSet,
     * полученные из запросов в этом соединении, поэтому все взаимодействие с БД должно быть ограничено до закрытия.
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException exc) {
            System.out.println("Caught exception during work with database.");
            ServerMain.logException(exc);
        }
    }

    /**
     * Заполнение prepared-выражения множеством значений (чисел, строк, логических значений)
     * @param statement - SQL-выражение со знаками ? (prepared)
     * @param preps - значения, которыми необходимо заполнить выражение
     */
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
