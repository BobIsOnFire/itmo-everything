package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.FileIOHelper;
import com.bobisonfire.foodshell.ServerHelper;
import com.bobisonfire.foodshell.ServerMain;
import com.bobisonfire.foodshell.entity.*;
import com.bobisonfire.foodshell.exc.*;
import com.bobisonfire.foodshell.transformer.ObjectTransformer;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
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
     * @throws CommandNotFoundException Послано, если такой команды не существует.
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
            s.writeToChannel(socket, gender.getName());
    }

    /**
     * Создание новой локации или перезаписывание существующей с заданными характеристиками.<br>
     * Все локации сохраняются в соответствующую коллекцию в формате CSV (путь к коллекции задается
     * пользователем), доступ к информации о состоянии локаций осуществляется командой show.<br>
     * <br>
     * Использование команды: insert name object
     * @param name Название локации (уникальный ключ)
     * @param object json-объект, содержащий поля "x", "y", "z" с координатами локации - числа с плавающей запятой.
     *               Если какого-то из полей не будет, оно автоматически заполнится нулем.
     *               Остальные поля игнорируются.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    public void insert(String name, ObjectTransformer object) {
        List<ObjectTransformer> list = f.readCSVListFromFile(map.get("path"));

        object.put("name", name);
        Human newHuman = new Human(object);

        List<Human> humanList = new ArrayList<>();
        list.forEach(elem -> humanList.add( new Human(elem) ));

        List<Human> humanListC = humanList
                .stream()
                .filter(elem -> !elem.equals(newHuman))
                .collect(Collectors.toList());

        humanListC.add(newHuman);

        f.writeCSVListIntoFile(humanListC, map.get("path"));
    }

    public void location(String name, Coordinate coords) {
        List<ObjectTransformer> list = f.readCSVListFromFile(Location.PATH);

        Location newLoc = new Location(name, coords);

        List<Location> locationList = new ArrayList<>();
        list.forEach(elem -> locationList.add( new Location(elem) ));

        List<Location> locationListC = locationList
                .stream()
                .filter(elem -> !elem.equals(newLoc))
                .collect(Collectors.toList());

        locationListC.add(newLoc);

        f.writeCSVListIntoFile(locationListC, Location.PATH);
    }

    public void locations() {
        f.readCSVListFromFile(Location.PATH)
                .forEach(elem -> s.writeToChannel( socket, new Location(elem).toString() ));
    }

    /**
     * Просмотр списка имен и координат существующих локаций.<br>
     * <br>
     * Использование команды: show
     */
    public void show() {
        f.readCSVListFromFile( map.get("path") )
                .forEach(elem -> s.writeToChannel( socket, new Human(elem).toString()));
    }

    /**
     * Просмотр данных о текущем пользователе, а именно гендер, имя персонажа, его возраст (в годах),
     * уровень текущего насыщения, настроения, его местоположение и точные координаты.<br>
     * <br>
     * Использование команды: me
     */
    public void me() {
        Human me = Human.getHumanByName(map.get("logUser"), map.get("path"));
        Coordinate coordinate = me.getLocation().getCoords();

        s.writeToChannel(socket, String.format( Locale.US,

        "%s %s, %d лет.\n" +
                "Местоположение - %s, точные координаты - %s.",

                me.getGender().getName(), me.getName(), me.getAge(),
                me.getLocation().getName(), coordinate
        ) );
    }

    /**
     * Залогиниться за другого пользователя-персонажа.<br>
     * <br>
     * Использование команды: login name
     * @param name Имя персонажа (уникальный ключ).
     */
    public void login(String name) {
        Human.getHumanByName(name, map.get("path"));
        map.put("logUser", name);
    }

    /**
     * Переместить текущего пользователя-персонажа в заданную локацию.<br>
     * <br>
     * Использование команды: move location
     * @param location Название локации (уникальный ключ).
     */
    public void move(String location) {
        Location loc = Location.getLocationByName(location);

        List<Human> humanList = new ArrayList<>();
        f.readCSVListFromFile(map.get("path")).forEach(elem -> {
            Human human = new Human(elem);
            if ( human.getName().equals(map.get("logUser")) )
                human.setLocation(loc);
            humanList.add(human);
        });

        f.writeCSVListIntoFile(humanList, map.get("path"));
    }

    /**
     * Уничтожение локации по ее ключу. Все персонажи, которые были в этой локации, перемещаются в локацию World.<br>
     * Локацию World уничтожить нельзя (появляется соответствующее сообщение).<br>
     * <br>
     * Использование команды: remove name
     * @param name Название локации (уникальный ключ).
     */
    public void remove(String name) {
        if (name.equals("God")) {
            s.writeToChannel(socket, "God невозможно уничтожить (по крайней мере, тебе)");
            return;
        }
        List<Human> humanList = new ArrayList<>();

        f.readCSVListFromFile(map.get("path"))
                .stream()
                .filter(elem -> !elem.getString("name").equals(name))
                .forEach(elem -> humanList.add( new Human(elem) ));

        f.writeCSVListIntoFile(humanList, map.get("path"));
    }

    /**
     * Уничтожение всех локаций (кроме World), находящихся выше чем заданная метка в мире.<br>
     * Все персонажи из уничтоженных локаций перемещаются в локацию World.<br>
     * <br>
     * Использование команды: remove_greater object
     * @param object Метка в пространстве в формате json-объекта, содержащего поля "x", "y", "z"
     *               со значениями чисел с плавающей запятой.<br>
     *               Если какого-то из полей не будет, оно автоматически заполнится нулем.
     *               Остальные поля игнорируются.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    public void remove_greater(ObjectTransformer object) {
        List<Human> list = new ArrayList<>();
        Human compare = new Human(object);

        f.readCSVListFromFile(map.get("path"))
                .forEach(elem -> list.add( new Human(elem) ));

        List<Human> humanList = list
                .stream()
                .filter(elem -> elem.equals( new Human() ) || elem.compareTo(compare) > 0)
                .collect(Collectors.toList());

        f.writeCSVListIntoFile(humanList, map.get("path"));
    }

    /**
     * Уничтожение всех локаций (кроме World), находящихся ниже чем заданная метка в мире.<br>
     * Все персонажи из уничтоженных локаций перемещаются в локацию World.<br>
     * <br>
     * Использование команды: remove_lower object
     * @param object Метка в пространстве в формате json-объекта, содержащего поля "x", "y", "z"
     *               со значениями чисел с плавающей запятой.<br>
     *               Если какого-то из полей не будет, оно автоматически заполнится нулем.
     *               Остальные поля игнорируются.
     * @throws TransformerException Послано, если object не является json-объектом.
     */
    public void remove_lower(ObjectTransformer object) {
        List<Human> list = new ArrayList<>();
        Human compare = new Human(object);

        f.readCSVListFromFile(map.get("path"))
                .forEach(elem -> list.add( new Human(elem) ));

        List<Human> humanList = list
                .stream()
                .filter(elem -> elem.equals( new Human() ) || elem.compareTo(compare) < 0)
                .collect(Collectors.toList());

        f.writeCSVListIntoFile(humanList, map.get("path"));
    }

    /**
     * Очистка коллекции с локациями a.k.a. уничтожение всех локаций, кроме World.<br>
     * При этом время создания локации не перезаписывается.<br>
     * <br>
     * Использование команды: clear
     */
    public void clear() {
        List<Human> humanList = new ArrayList<>();
        humanList.add(new Human());

        f.writeCSVListIntoFile(humanList, map.get("path"));
    }

    /**
     * Получение информации о состоянии текущей коллекции a.k.a. состоянии текущего мира.<br>
     * Информация: дата создания мира, тип коллекции, количество локаций.<br>
     * <br>
     * Использование команды: info
     */
    public void info() {
//        todo оформить получше дату создания коллекции

//        try {
//            File file = new File(Location.PATH);
//            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
//            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
//            String creationDate = sdf.format(attributes.creationTime().toMillis());
//            con.out.println("Дата создания: " + creationDate);
//        }
//        catch (IOException exc) {
//            con.out.println("Невозможно определить дату создания.");
//        }

        int mapSize = f.readCSVListFromFile(map.get("path")).size();

        s.writeToChannel(socket, "Тип коллекции: соответствие в виде дерева существующих локаций и их имен.\n" +
                "Размер коллекции: " + mapSize );
    }
}
