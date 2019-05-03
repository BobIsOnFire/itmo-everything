package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.ServerHelper;
import com.bobisonfire.foodshell.ServerMain;
import com.bobisonfire.foodshell.entity.*;
import com.bobisonfire.foodshell.exc.*;
import com.bobisonfire.foodshell.transformer.CSVObject;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.io.*;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    void exit() {
        s.writeToChannel(socket, "FoodShell закрывается.");
    }

    /**
     * Просмотр списка всех существующих команд.<br>
     * <br>
     * Использование команды: help
     */
    void help() {
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
    void help(String command) {
        s.writeToChannel( socket, Command.getCommandByName(command).getDescription() );
    }

    /**
     * Выводит список всех (нет) существующих гендеров.<br>
     * <br>
     * Использование команды: gender
     */
    void gender() {
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
    void insert(String name, ObjectTransformer object) {
        object.put("name", name);
        Human newHuman = new Human(object);

        Set<Human> humanSet;
        try {
            humanSet = Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(x -> new Human( new CSVObject(Human.CSV_HEAD, x) ))
                    .filter(x -> !x.equals(newHuman) )
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            humanSet = new TreeSet<>();
        }

        humanSet.add(newHuman);
        List<String> list = humanSet.stream()
                .map(Human::toCSV)
                .collect(Collectors.toList());

        list.add(0, Human.CSV_HEAD);
        try {
            Files.write(Paths.get(Human.PATH), list);
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }

        System.out.println(map.get("name") + " добавил персонажа в коллекцию: " +
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
    void location(String name, Coordinate coords) {
        Location newLoc = new Location(name, coords);

        Set<Location> locationSet;
        try {
            locationSet = Files.lines(Paths.get(Location.PATH))
                    .skip(1)
                    .map(x -> new Location( new CSVObject(Location.CSV_HEAD, x) ))
                    .filter(x -> !x.equals(newLoc) )
                    .collect(Collectors.toCollection(TreeSet::new));
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            locationSet = new TreeSet<>();
        }

        locationSet.add(newLoc);
        List<String> list = locationSet.stream()
                .map(Location::toCSV)
                .collect(Collectors.toList());

        list.add(0, Location.CSV_HEAD);
        try {
            Files.write(Paths.get(Location.PATH), list);
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }

        System.out.println(map.get("name") + " добавил локацию в коллекцию: " +
                newLoc.toString() );
    }

    /**
     * Просмотр списка имен и координат существующих локаций.<br>
     * <br>
     * Использование команды: locations
     */
    void locations() {
        try {
            Files.lines(Paths.get(Location.PATH))
                    .skip(1)
                    .map(elem -> new Location( new CSVObject(Location.CSV_HEAD, elem) ))
                    .forEach(elem -> s.writeToChannel( socket, elem.toString() ));
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
        }
    }

    /**
     * Просмотр списка имен и характеристик существующих персонажей.<br>
     * <br>
     * Использование команды: show
     */
    void show() {
        try {
            Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(x -> new Human( new CSVObject(Human.CSV_HEAD, x) ))
                    .sorted()
                    .forEach(elem -> s.writeToChannel( socket, elem.toString() ));
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
        }
    }

    /**
     * Просмотр данных о текущем пользователе, а именно: гендер, имя персонажа, его возраст (в годах),
     * его местоположение, точные координаты и дата создания объекта, описывающего персонажа.<br>
     * <br>
     * Использование команды: me
     */
    void me() {
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
    void login(String name) {
        Human.getHumanByName(name, Human.PATH);
        String previous = map.put("logUser", name);
        System.out.println(map.get("name") + " сменил персонажа: " + previous + " -> " + name);
    }

    /**
     * Переместить текущего пользователя-персонажа в заданную локацию.<br>
     * <br>
     * Использование команды: move location
     * @param location Название локации (уникальный ключ).
     */
    void move(String location) {
        Location loc = Location.getLocationByName(location);

        List<String> list;
        try {
            list = Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(x -> new Human( new CSVObject(Human.CSV_HEAD, x) ))
                    .sorted()
                    .peek(elem -> {
                        if (elem.getName().equals(map.get("logUser"))) elem.setLocation(loc);
                    })
                    .map(Human::toCSV)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            list = new ArrayList<>();
        }

        list.add(0, Human.CSV_HEAD);
        try {
            Files.write(Paths.get(Human.PATH), list);
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }
        System.out.println(map.get("name") + " переместил персонажа " + map.get("logUser") + " в " + location);
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
            s.writeToChannel(socket, "God невозможно уничтожить (по крайней мере, тебе)");
            return;
        }

        List<String> list;
        try {
            list = Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(elem -> new Human( new CSVObject(Human.CSV_HEAD, elem)))
                    .sorted()
                    .filter(elem -> !elem.getName().equals(name))
                    .map(Human::toCSV)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            list = new ArrayList<>();
        }

        list.add(0, Human.CSV_HEAD);
        try {
            Files.write(Paths.get(Human.PATH), list);
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }
        System.out.println(map.get("name") + " уничтожил персонажа " + name);

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

        List<String> list;
        try {
            list = Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(elem -> new Human( new CSVObject(Human.CSV_HEAD, elem)))
                    .sorted()
                    .filter(elem -> elem.equals( new Human() ) || elem.compareTo(compare) > 0)
                    .map(Human::toCSV)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            list = new ArrayList<>();
        }

        list.add(0, Human.CSV_HEAD);
        try {
            Files.write(Paths.get(Human.PATH), list);
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }
        System.out.println(map.get("name") + " уничтожил персонажей, родившихся раньше чем " +
                compare.getBirthday() + ". Текущий размер коллекции: " + list.size() );
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

        List<String> list;
        try {
            list = Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(elem -> new Human( new CSVObject(Human.CSV_HEAD, elem)))
                    .sorted()
                    .filter(elem -> elem.equals( new Human() ) || elem.compareTo(compare) < 0)
                    .map(Human::toCSV)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            list = new ArrayList<>();
        }

        list.add(0, Human.CSV_HEAD);
        try {
            Files.write(Paths.get(Human.PATH), list);
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }
        System.out.println(map.get("name") + " уничтожил персонажей, родившихся позже чем " +
                compare.getBirthday() + ". Размер коллекции: " + list.size() );
    }

    /**
     * Очистка коллекции с персонажами a.k.a. уничтожение всех персонажей, кроме God.<br>
     * При этом время создания коллекции перезаписывается.<br>
     * <br>
     * Использование команды: clear
     */
    void clear() {
        try {
            Files.write(Paths.get(Human.PATH), Arrays.asList( Human.CSV_HEAD, new Human().toCSV() ));
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при записи.");
        }
        System.out.println(map.get("name") + " очистил коллекцию персонажей.");
    }

    /**
     * Получение информации о состоянии текущей коллекции a.k.a. состоянии текущего мира.<br>
     * Информация: дата создания мира, тип коллекции, количество персонажей.<br>
     * <br>
     * Использование команды: info
     */
    void info() {
        Set<Human> set;
        try {
            set = Files.lines(Paths.get(Human.PATH))
                    .skip(1)
                    .map(elem -> new Human( new CSVObject(Human.CSV_HEAD, elem) ))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении.");
            set = new TreeSet<>();
        }

        Human first = set.stream()
                .filter(elem -> elem.getName().equals("God"))
                .findFirst()
                .orElse(new Human());

        int mapSize = set.size();
        s.writeToChannel(socket, "Тип коллекции: " + TreeSet.class.getCanonicalName() + ".\n" +
                "Размер коллекции: " + mapSize + ", дата создания - " + first.getCreationDate() );
    }

    /**
     * Загрузить коллекцию на сервер. Данные из коллекции сохраняются в путь по умолчанию или в путь,
     * указанный при создании сервера.<br>
     * <br>
     * Использование команды: export path
     */
    void export() {
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

            System.out.println(map.get("name") + " загрузил свою коллекцию на сервер.");
        } catch (IOException e) {
            s.writeToChannel(socket, "Невозможно записать в файл " + Human.PATH);
        }
    }

    /**
     * Сохранить коллекцию в файл на клиенте. Данные для сохранения берутся из файла по умолчанию или файла,
     * указанного при создании сервера.<br>
     * <br>
     * Использование команды: import path
     */
    void _import(String path) {
        try (Scanner scanner = new Scanner(new FileReader(Human.PATH))) {
            s.writeToChannel(socket, "!import " + path);
            while (scanner.hasNextLine())
                s.writeToChannel(socket, scanner.nextLine() );
            s.writeToChannel(socket, "!endimport");

            System.out.println(map.get("name") + " импортировал свою коллекцию с сервера.");
        } catch (IOException e) {
            s.writeToChannel(socket,"Невозможно прочитать файл " + Human.PATH);
        }
    }

    /**
     * Сохранить коллекцию в файл на сервере. Данные для сохранения берутся из файла по умолчанию или файла,
     * указанного при создании сервера.<br>
     * <br>
     * Использование команды: save path
     */
    void save(String path) {
        try {
            Files.write(Paths.get(path), Files.readAllLines(Paths.get(Human.PATH)));
            System.out.println(map.get("name") + " сохранил коллекцию в " + path + ".");
        } catch (IOException exc) {
            s.writeToChannel(socket, "Произошла ошибка при чтении или записи файла.");
        }
    }

    /**
     * Загрузить коллекцию из файла на сервере. Данные из коллекции сохраняются в путь по умолчанию или в путь,
     * указанный при создании сервера.<br>
     * <br>
     * Использование команды: load path
     */
    void load(String path) {
        try {
            Files.write(Paths.get(Human.PATH), Files.readAllLines(Paths.get(path)));
            System.out.println(map.get("name") + " загрузил коллекцию из " + path + ".");
        } catch (IOException exc) {
            s.writeToChannel(socket, "Произошла ошибка при чтении или записи файла.");
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
                    s.writeToChannel(socket, scanner.nextLine());
                scanner.close();
            }
        } catch (IOException e) {
            s.writeToChannel(socket, "Произошла ошибка при чтении файла.");
        }
    }
}
