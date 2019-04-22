package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.FileIOHelper;
import com.bobisonfire.foodshell.ServerHelper;
import com.bobisonfire.foodshell.ServerMain;
import com.bobisonfire.foodshell.entity.*;
import com.bobisonfire.foodshell.exc.*;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Список команд для <i>FoodShell</i>.
 * Работа с <i>FoodShell</i>: пользователь является одним из персонажей внутренней вселенной.<br>
 * Изначально существует только персонаж God с безумными характеристиками и локация World, являющаяся корнем мира.<br>
 * Вселенную можно наполнять новыми локациями и новыми персонажами, персонажи взаимодействуют между собой и с локациями.<br>
 * Все данные, характеризующие состояние мира, находятся в CSV-таблицах.
 */
public class CommandDoc {
    private static ServerHelper s = ServerMain.server;
    private static FileIOHelper f = new FileIOHelper();
    private SocketChannel socket;
    private Map<String, String> map;

    public CommandDoc(SelectionKey key) {
        socket = (SocketChannel) key.channel();
        map = (Map<String, String>) key.attachment();
    }

    /**
     * Завершить работу с <i>FoodShell</i>.<br>
     * <br>
     * Использование команды: exit
     */
    public void exit() {
        s.writeToChannel(socket, "FoodShell закрывается.");
    }

    /**
     * Просмотр списка всех существующих команд.<br>
     * <br>
     * Использование команды: help
     */
    public void help() {
        s.writeToChannel(socket, "Используйте help command для получения описания определенной команды.");
        Command.getMap().forEach( (k, v) -> s.writeToChannel(socket, k));
    }

    /**
     * Просмотр информации о команде.<br>
     * <br>
     * Использование команды: help command
     * @param command Название команды
     * @throws NotFoundException Послано, если такой команды не существует.
     */
    public void help(String command) {
        s.writeToChannel( socket, Command.getCommandByName(command).getDescription() );
    }

    /**
     * Выводит список всех (нет) существующих гендеров.<br>
     * <br>
     * Использование команды: gender
     */
    public void gender() {
        s.writeToChannel(socket, "\t\t** There are only 15 genders. **");
        for (Gender gender: Gender.values())
            s.writeToChannel(socket, gender.ordinal() + ". " + gender.getName());
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
    public void insert(String name, ObjectTransformer object) {
        Set<ObjectTransformer> set = f.readCSVSetFromFile(Human.PATH);

        object.put("name", name);
        Human newHuman = new Human(object);
        newHuman.setCreationDate(new Date());

        Set<Human> humanSet = new TreeSet<>();
        set.forEach(elem -> humanSet.add( new Human(elem) ));

        Set<Human> humanSetC = humanSet
                .stream()
                .filter(elem -> !elem.equals(newHuman))
                .collect(Collectors.toSet());

        humanSetC.add(newHuman);
        humanSetC = new TreeSet<>(humanSetC);

        f.writeCSVSetIntoFile(humanSetC, Human.PATH);
        System.out.println("Пользователь " + map.get("name") + " добавил персонажа в коллекцию: " +
                newHuman.toString() );
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
    public void location(String name, Coordinate coords) {
        Set<ObjectTransformer> set = f.readCSVSetFromFile(Location.PATH);

        Location newLoc = new Location(name, coords);

        Set<Location> locationSet = new TreeSet<>();
        set.forEach(elem -> locationSet.add( new Location(elem) ));

        Set<Location> locationSetC = locationSet
                .stream()
                .filter(elem -> !elem.equals(newLoc))
                .collect(Collectors.toSet());

        locationSetC.add(newLoc);
        locationSetC = new TreeSet<>(locationSet);

        f.writeCSVSetIntoFile(locationSetC, Location.PATH);
        System.out.println("Пользователь " + map.get("name") + " добавил локацию в коллекцию " +
                Location.PATH + ": " + newLoc.toCSV() );
    }

    /**
     * Просмотр списка имен и координат существующих локаций.<br>
     * <br>
     * Использование команды: locations
     */
    public void locations() {
        f.readCSVSetFromFile(Location.PATH)
                .forEach(elem -> s.writeToChannel( socket, new Location(elem).toString() ));
    }

    /**
     * Просмотр списка имен и характеристик существующих персонажей.<br>
     * <br>
     * Использование команды: show
     */
    public void show() {
        Set<Human> humanSet = new TreeSet<>();

        f.readCSVSetFromFile( Human.PATH )
                .forEach(elem -> humanSet.add( new Human(elem) ));

        humanSet.forEach(elem -> s.writeToChannel( socket, elem.toString() ));
    }

    /**
     * Просмотр данных о текущем пользователе, а именно: гендер, имя персонажа, его возраст (в годах),
     * его местоположение, точные координаты и дата создания объекта, описывающего персонажа.<br>
     * <br>
     * Использование команды: me
     */
    public void me() {
        Human me = Human.getHumanByName(map.get("logUser"), Human.PATH);
        Coordinate coordinate = me.getLocation().getCoords();

        s.writeToChannel(socket, String.format( Locale.US,

        "%s %s, %d лет.\n" +
                "Местоположение - %s, точные координаты - %s.\n" +
                "Дата создания - %s.",

                me.getGender().getName(), me.getName(), me.getAge(),
                me.getLocation().getName(), coordinate,
                me.getCreationDate()
        ) );
    }

    /**
     * Залогиниться за другого пользователя-персонажа.<br>
     * <br>
     * Использование команды: login name
     * @param name Имя персонажа (уникальный ключ).
     */
    public void login(String name) {
        Human.getHumanByName(name, Human.PATH);
        String previous = map.put("logUser", name);
        System.out.println("Пользователь " + map.get("name") + " сменил персонажа: " + previous + " -> " + name);
    }

    /**
     * Переместить текущего пользователя-персонажа в заданную локацию.<br>
     * <br>
     * Использование команды: move location
     * @param location Название локации (уникальный ключ).
     */
    public void move(String location) {
        Location loc = Location.getLocationByName(location);

        Set<Human> humanSet = new TreeSet<>();
        f.readCSVSetFromFile(Human.PATH).forEach(elem -> {
            Human human = new Human(elem);
            if ( human.getName().equals(map.get("logUser")) )
                human.setLocation(loc);
            humanSet.add(human);
        });

        f.writeCSVSetIntoFile(humanSet, Human.PATH);
        System.out.println("Пользователь " + map.get("name") + " переместил персонажа " + map.get("logUser") + " в " + location);
    }

    /**
     * Уничтожение персонажа по его ключу.<br>
     * Персонажа God уничтожить нельзя (появляется соответствующее сообщение).<br>
     * <br>
     * Использование команды: remove name
     * @param name Имя персонажа (уникальный ключ).
     */
    public void remove(String name) {
        if (name.equals("God")) {
            s.writeToChannel(socket, "God невозможно уничтожить (по крайней мере, тебе)");
            return;
        }
        Set<Human> humanSet = new TreeSet<>();
        Set<ObjectTransformer> set = f.readCSVSetFromFile(Human.PATH);

        set.stream()
                .filter(elem -> !elem.getString("name").equals(name))
                .forEach(elem -> humanSet.add( new Human(elem) ));

        if (set.size() == humanSet.size()) {
            s.writeToChannel(socket, "Персонаж не уничтожен: его не существует!");
        } else {
            f.writeCSVSetIntoFile(humanSet, Human.PATH);
            System.out.println("Пользователь " + map.get("name") + " уничтожил персонажа " + name);
        }
    }

    /**
     * Уничтожение всех персонажей (кроме God), которые старше данного персонажа.<br>
     * <br>
     * Использование команды: remove_older object
     * @param object json-объект, описывающий персонажа (см. insert), относительно которого
     *               производится уничтожение персонажей.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    public void remove_older(ObjectTransformer object) {
        Set<Human> set = new TreeSet<>();
        Human compare = new Human(object);

        f.readCSVSetFromFile(Human.PATH)
                .forEach(elem -> set.add( new Human(elem) ));

        Set<Human> humanSet = set
                .stream()
                .filter(elem -> elem.equals( new Human() ) || elem.compareTo(compare) > 0)
                .collect(Collectors.toSet());

        humanSet = new TreeSet<>(humanSet);

        f.writeCSVSetIntoFile(humanSet, Human.PATH);
        System.out.println("Пользователь " + map.get("name") + " уничтожил персонажей, родившихся раньше чем " +
                compare.getBirthday() + ". Уничтожено персонажей: " + ( set.size() - humanSet.size() ) +
                ". Размер коллекции: " + humanSet.size() );
    }

    /**
     * Уничтожение всех персонажей (кроме God), которые младше данного персонажа.<br>
     * <br>
     * Использование команды: remove_younger object
     * @param object json-объект, описывающий персонажа (см. insert), относительно которого
     *               производится уничтожение персонажей.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    public void remove_younger(ObjectTransformer object) {
        Set<Human> set = new TreeSet<>();
        Human compare = new Human(object);

        f.readCSVSetFromFile(Human.PATH)
                .forEach(elem -> set.add( new Human(elem) ));

        Set<Human> humanSet = set
                .stream()
                .filter(elem -> elem.equals( new Human() ) || elem.compareTo(compare) < 0)
                .collect(Collectors.toSet());

        humanSet = new TreeSet<>(humanSet);

        f.writeCSVSetIntoFile(humanSet, Human.PATH);
        System.out.println("Пользователь " + map.get("name") + " уничтожил персонажей, родившихся позже чем " +
                compare.getBirthday() + ". Уничтожено персонажей: " + ( set.size() - humanSet.size() ) +
                ". Размер коллекции: " + humanSet.size() );
    }

    /**
     * Очистка коллекции с персонажами a.k.a. уничтожение всех персонажей, кроме God.<br>
     * При этом время создания коллекции перезаписывается.<br>
     * <br>
     * Использование команды: clear
     */
    public void clear() {
        Set<Human> humanSet = new TreeSet<>();
        humanSet.add(new Human());

        f.writeCSVSetIntoFile(humanSet, Human.PATH);
        System.out.println("Пользователь " + map.get("name") + " очистил коллекцию персонажей.");
    }

    /**
     * Получение информации о состоянии текущей коллекции a.k.a. состоянии текущего мира.<br>
     * Информация: дата создания мира, тип коллекции, количество персонажей.<br>
     * <br>
     * Использование команды: info
     */
    public void info() {
        Set<ObjectTransformer> set = f.readCSVSetFromFile(Human.PATH);
        Optional<ObjectTransformer> opt = set.stream().filter(elem -> elem.getString("name").equals("God")).findFirst();

        Date creationDate;
        if (opt.isPresent())
            creationDate = opt.get().getDate("creationDate", "dd.MM.yyyy HH:mm:ss");
        else
            creationDate = new Date();


        int mapSize = set.size();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        s.writeToChannel(socket, "Тип коллекции: " + new TreeSet<Human>().getClass().getCanonicalName() + ".\n" +
                "Размер коллекции: " + mapSize + ", дата создания - " + sdf.format(creationDate) );
    }

    /**
     * Загрузить коллекцию на сервер. Данные из коллекции сохраняются в путь по умолчанию или в путь,
     * указанный при создании сервера.<br>
     * <br>
     * Использование команды: export path
     */
    public void export() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Human.PATH, false))) {
            while ( true ) {
                String str = s.readFromChannel(socket);
                if (str.contains("!endexport")) {
                    str = str.substring(0, str.indexOf("!endexport"));
                    writer.write(str);
                    break;
                }

                writer.write(str);
            }

            System.out.println("Пользователь " + map.get("name") + " загрузил свою коллекцию на сервер.");
        } catch (IOException e) {
            System.out.println("Невозможно записать в файл " + Human.PATH);
        }
    }

    /**
     * Сохранить коллекцию с сервера в файл. Данные для сохранения берутся из файла по умолчанию или файла,
     * указанного при создании сервера.<br>
     * <br>
     * Использование команды: import path
     */
    public void _import(String path) {
        try (Scanner scanner = new Scanner(new FileReader(Human.PATH))) {
            s.writeToChannel(socket, "!import " + path);
            while (scanner.hasNextLine())
                s.writeToChannel(socket, scanner.nextLine() );
            s.writeToChannel(socket, "!endimport");

            System.out.println("Пользователь " + map.get("name") + " импортировал свою коллекцию с сервера.");
        } catch (IOException e) {
            System.out.println("Невозможно прочитать файл " + Human.PATH);
        }
    }
}
