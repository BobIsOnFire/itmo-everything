package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.DBExchanger;
import com.bobisonfire.foodshell.ServerHelper;
import com.bobisonfire.foodshell.ServerMain;
import com.bobisonfire.foodshell.entity.*;
import com.bobisonfire.foodshell.exc.*;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;
import com.bobisonfire.foodshell.transformer.SQLObject;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

/**
 * Список команд для <i>FoodShell</i>.
 * Работа с <i>FoodShell</i>: пользователь является одним из персонажей внутренней вселенной.<br>
 * Изначально существует только персонаж God с безумными характеристиками и локация World, являющаяся корнем мира.<br>
 * Вселенную можно наполнять новыми локациями и новыми персонажами, персонажи взаимодействуют между собой и с локациями.<br>
 * Все данные, характеризующие состояние мира, находятся в CSV-таблицах.
 */
public class CommandDoc {
    private SocketChannel socket;
    private int id;

    public CommandDoc(SelectionKey key) {
        socket = (SocketChannel) key.channel();
        id = (Integer) key.attachment();
    }

    /**
     * Завершить работу с <i>FoodShell</i>.<br>
     * <br>
     * Использование команды: exit
     */
    void exit() {
        ServerHelper.writeToChannel(socket, "FoodShell закрывается.");
    }

    /**
     * Просмотр списка всех существующих команд.<br>
     * <br>
     * Использование команды: help
     */
    void help() {
        ServerHelper.writeToChannel(socket, "Используйте help command для получения описания определенной команды.");
        Command.getMap().forEach( (k, v) -> ServerHelper.writeToChannel(socket, k));
    }

    /**
     * Просмотр информации о команде.<br>
     * <br>
     * Использование команды: help command
     * @param command Название команды
     * @throws NotFoundException Послано, если такой команды не существует.
     */
    void help(String command) {
        ServerHelper.writeToChannel( socket, Command.getCommandByName(command).getDescription() );
    }

    /**
     * Выводит список всех (нет) существующих гендеров.<br>
     * <br>
     * Использование команды: gender
     */
    void gender() {
        ServerHelper.writeToChannel(socket, "\t\t** There are only 15 genders. **");
        for (Gender gender: Gender.values())
            ServerHelper.writeToChannel(socket, gender.ordinal() + ". " + gender.getName());
    }

    /**
     * Создание нового персонажа или перезаписывание существующего с заданными характеристиками.<br>
     * Все персонажи сохраняются в соответствующую коллекцию в формате CSV (путь к коллекции задается
     * пользователем), доступ к информации о состоянии персонажей осуществляется командой show.<br>
     * <br>
     * Использование команды: insert name object
     * @param name Имя персонажа (уникальный ключ)
     * @param object json-объект, содержащий следующие поля:<br>
     *               <b>birthday</b> - дата рождения в формате ДД.ММ.ГГГГ, по умолчанию - сегодняшняя дата;<br>
     *               <b>gender</b> - число от 0 до 14, соответствущее номеру гендера (команда gender для
     *               просмотра соответствия), по умолчанию - 0 (Male);<br>
     *               <b>location</b> - название локации (уникальный ключ), в которой изначально находится персонаж
     *               (просмотр существующих локаций - locations), по умолчанию - World.<br>
     *               Если поле не указано, ставится значение по умолчанию. Остальные поля игнорируются.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    void insert(String name, ObjectTransformer object) {
        object.put("name", name);
        Human human = new Human(object);

        try (DBExchanger exchanger = new DBExchanger()) {
            exchanger.update(
                    "INSERT INTO humans (creator_id, name, birthday, gender, location) " +
                    String.format( "VALUES (%d,'%s','%s',%d,'%s')",
                            id, human.getName(), human.getBirthday("yyyy-MM-dd"),
                            human.getGender().ordinal(), human.getLocation()
                    ) +
                    "ON CONFLICT ON CONSTRAINT humans_creator_id_name_key DO UPDATE SET " +
                        "birthday=EXCLUDED.birthday, gender=EXCLUDED.gender," +
                        "location=EXCLUDED.location, creation_date=current_timestamp;");
        }

        System.out.println("User#" + id + " добавил персонажа в коллекцию: " + human.toString() );
    }

    /**
     * Создание новой локации или перезаписывание текущей с заданными характеристиками.<br>
     * Все локации сохраняются в соответствующую коллекцию в формате CSV, доступ к ней осуществляется
     * командой locations.<br>
     * <br>
     * Использование команды: location name x y z
     * @param name - название локации (уникальный ключ)
     * @param coords - координаты локации (три числа с плавающей точкой)
     */
    void location(String name, Coordinate coords) {
        Location location = new Location(name, coords);
        Coordinate coordinate = location.getCoords();

        try (DBExchanger exchanger = new DBExchanger()) {
            exchanger.update(
                    "INSERT INTO locations (name, x, y, z) " +
                            String.format( Locale.US, "VALUES ('%s',%.3f,%.3f,%.3f)",
                                    location.getName(), coordinate.getX(),
                                    coordinate.getY(), coordinate.getZ()
                            ) +
                            "ON CONFLICT ON CONSTRAINT locations_pkey DO UPDATE SET " + // todo переименовать конфликты, т.к. хелиос возможно сделает другие названия
                            "x=EXCLUDED.x, y=EXCLUDED.y, z=EXCLUDED.z;");
        }

        System.out.println("User#" + id + " добавил локацию в коллекцию: " + location.toString() );
    }

    /**
     * Просмотр списка имен и координат существующих локаций.<br>
     * <br>
     * Использование команды: locations
     */
    void locations() {
        try (DBExchanger exchanger = new DBExchanger()) {
            ResultSet locationSet = exchanger.getQuery("SELECT * FROM locations ORDER BY name;");
            while (locationSet.next()) {
                Location location = new Location(locationSet);
                ServerHelper.writeToChannel(socket, location.toString());
            }
        } catch (SQLException exc) {
            System.out.println("Произошла ошибка доступа к базе данных.");
            ServerMain.logException(exc);
        }
    }

    /**
     * Просмотр списка имен и характеристик существующих персонажей.<br>
     * <br>
     * Использование команды: show
     */
    void show() {
        try (DBExchanger exchanger = new DBExchanger()) {
            ResultSet humanSet = exchanger.getQuery("SELECT * FROM humans ORDER BY birthday;");
            while (humanSet.next()) {
                Human human = new Human(new SQLObject( humanSet ));
                ServerHelper.writeToChannel(socket, human.toString());
            }
        } catch (SQLException exc) {
            System.out.println("Произошла ошибка доступа к базе данных.");
            ServerMain.logException(exc);
        }
    }

    void show(int creatorId) {
        try (DBExchanger exchanger = new DBExchanger()) {
            ResultSet humanSet = exchanger.getQuery("SELECT * FROM humans WHERE creator_id=? ORDER BY birthday;", creatorId);
            while (humanSet.next()) {
                Human human = new Human(new SQLObject( humanSet ));
                ServerHelper.writeToChannel(socket, human.toString());
            }
        } catch (SQLException exc) {
            System.out.println("Произошла ошибка доступа к базе данных.");
            ServerMain.logException(exc);
        }
    }

    /**
     * Просмотр данных о текущем пользователе, а именно: гендер, имя персонажа, его возраст (в годах),
     * его местоположение, точные координаты и дата создания объекта, описывающего персонажа.<br>
     * <br>
     * Использование команды: me
     */
    void me() {
        try (DBExchanger exchanger = new DBExchanger()) {
            ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE id=?;", id);
            set.next();
            ServerHelper.writeToChannel(socket,
                    "User#" + id + ": имя - " + set.getString("name") + "\n" +
                    "Email: " + set.getString("email") + "\n" +
                    "Время последней авторизации: " + set.getTimestamp("last_authorize")
            );
        } catch (SQLException exc) {
            System.out.println("Произошла ошибка доступа к базе данных.");
            ServerMain.logException(exc);
        }
    }

    /**
     * Переместить текущего пользователя-персонажа в заданную локацию.<br>
     * <br>
     * Использование команды: move location
     * @param location Название локации (уникальный ключ).
     */
    void move(String human, String location) {
        String destination = Location.getLocationByName(location);

        int rowsChanged;
        try (DBExchanger exchanger = new DBExchanger()) {
            rowsChanged = exchanger.update(
                    "UPDATE humans SET location=? WHERE creator_id=? AND name LIKE ?;",
                    destination, id, human
            );
        }
        if (rowsChanged > 0)
            System.out.println("User#" + id + " переместил персонажа " + human + " в " + location);
        else
            ServerHelper.writeToChannel(socket, "Перемещения не произошло: либо персонажа, либо локации не существует");
    }

    /**
     * Уничтожение персонажа по его ключу.<br>
     * Персонажа God уничтожить нельзя (появляется соответствующее сообщение).<br>
     * <br>
     * Использование команды: remove name
     * @param name Имя персонажа (уникальный ключ).
     */
    void remove(String name) {
        if (name.equals("God")) {
            ServerHelper.writeToChannel(socket, "God невозможно уничтожить (по крайней мере, тебе)");
            return;
        }

        int rowsChanged;
        try (DBExchanger exchanger = new DBExchanger()) {
            rowsChanged = exchanger.update(
                    "DELETE FROM humans WHERE creator_id=? AND name=?;",
                    id, name
            );
        }

        if (rowsChanged > 0)
            System.out.println("User#" + id + " уничтожил персонажа " + name);
        else
            ServerHelper.writeToChannel(socket, "Удаления не произошло: персонажа не существует.");
    }

    /**
     * Уничтожение всех персонажей (кроме God), которые старше данного персонажа.<br>
     * <br>
     * Использование команды: remove_older object
     * @param object json-объект, описывающий персонажа (см. insert), относительно которого
     *               производится уничтожение персонажей.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    void remove_older(ObjectTransformer object) {
        Human compare = new Human(object);

        int rowsChanged;
        try (DBExchanger exchanger = new DBExchanger()) {
            rowsChanged = exchanger.update(
                    "DELETE FROM humans WHERE creator_id=? AND date ? - birthday > 0;",
                    id, compare.getBirthday("yyyy-MM-dd")
            );
        }

        System.out.println("User#" + id + " уничтожил персонажей, родившихся раньше чем " +
                compare.getBirthday() + "\nУничтожено персонажей: " + rowsChanged);
    }

    /**
     * Уничтожение всех персонажей (кроме God), которые младше данного персонажа.<br>
     * <br>
     * Использование команды: remove_younger object
     * @param object json-объект, описывающий персонажа (см. insert), относительно которого
     *               производится уничтожение персонажей.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    void remove_younger(ObjectTransformer object) {
        Human compare = new Human(object);

        int rowsChanged;
        try (DBExchanger exchanger = new DBExchanger()) {
            rowsChanged = exchanger.update(
                    "DELETE FROM humans WHERE creator_id=? AND date ? - birthday < 0;",
                    id, compare.getBirthday("yyyy-MM-dd")
            );
        }

        System.out.println("User#" + id + " уничтожил персонажей, родившихся позже чем " +
                compare.getBirthday() + "\nУничтожено персонажей: " + rowsChanged);
    }

    /**
     * Очистка коллекции с персонажами a.k.a. уничтожение всех персонажей, кроме God.<br>
     * При этом время создания коллекции перезаписывается.<br>
     * <br>
     * Использование команды: clear
     */
    void clear() {
        try (DBExchanger exchanger = new DBExchanger()) {
            exchanger.update("DELETE FROM humans WHERE creator_id=?;", id);
        }
        System.out.println("User#" + id + " очистил свою коллекцию персонажей.");
    }

    /**
     * Получение информации о состоянии текущей коллекции a.k.a. состоянии текущего мира.<br>
     * Информация: дата создания мира, тип коллекции, количество персонажей.<br>
     * <br>
     * Использование команды: info
     */
    void info() {
        try (DBExchanger exchanger = new DBExchanger()) {
            ResultSet set = exchanger.getQuery("SELECT COUNT(*) FROM humans;");
            set.next();
            int totalElements = set.getInt(1);
            set.close();

            set = exchanger.getQuery("SELECT COUNT(*) FROM humans WHERE creator_id=?;", id);
            set.next();
            int currentUserElements = set.getInt(1);
            set.close();

            set = exchanger.getQuery("SELECT MIN(creation_date) FROM humans WHERE creator_id=?;", id);
            set.next();
            Instant creationTime = set.getTimestamp(1).toInstant();
            set.close();

            ServerHelper.writeToChannel(socket,
                    "Коллекция персонажей, хранится в базе PostgreSQL.\n" +
                    "Всего элементов - " + totalElements + ", для текущего пользователя - " + currentUserElements + ".\n" +
                    "Дата создания коллекции данным пользователем: " + creationTime.toString()
            );
        } catch (SQLException exc) {
            ServerMain.logException(exc);
        }
    }

    /**
     * Последовательно выводит содержимое файлов. Существует для отладки или чтения файлов коллекций.<br>
     * <br>
     * Использование команды: cat file1 [file2 ...]
     * @param files файлы, которые необходимо прочитать.
     */
    void cat(String... files) {
        try {
            for (String file: files) {
                Scanner scanner = new Scanner(new FileReader(file));
                while (scanner.hasNextLine())
                    ServerHelper.writeToChannel(socket, scanner.nextLine());
                scanner.close();
            }
        } catch (IOException e) {
            ServerHelper.writeToChannel(socket, "Произошла ошибка при чтении файла.");
        }
    }
}
