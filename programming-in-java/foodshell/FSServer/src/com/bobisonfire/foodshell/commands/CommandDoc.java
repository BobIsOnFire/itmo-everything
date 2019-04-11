package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.Connection;
import com.bobisonfire.foodshell.FileIOHelper;
import com.bobisonfire.foodshell.entity.*;
import com.bobisonfire.foodshell.exc.*;
import com.bobisonfire.foodshell.transformer.CSVObject;
import com.bobisonfire.foodshell.transformer.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Список команд для <i>FoodShell</i>.
 * Работа с <i>FoodShell</i>: пользователь является одним из персонажей внутренней вселенной.<br>
 * Изначально существует только персонаж God с безумными характеристиками и локация World, являющаяся корнем мира.<br>
 * Вселенную можно наполнять новыми локациями и новыми персонажами, персонажи взаимодействуют между собой и с локациями.<br>
 * Все данные, характеризующие состояние мира, находятся в CSV-таблицах.
 */
public class CommandDoc {
    /**
     * Завершить работу с <i>FoodShell</i>.<br>
     * <br>
     * Использование команды: exit
     */
    public boolean exit() {
        return false;
    }

    /**
     * История всех приемов пищи текущего пользователя и
     * его текущий уровень насыщения.<br>
     * <br>
     * Использование команды: meals
     */
    public boolean meals(Connection con) {
        return this.meals(con, con.getLogUser());
    }

    /**
     * История всех приемов пищи пользователя human и
     * его текущий уровень насыщения.<br>
     * <br>
     * Использование команды: meals human
     * @param human Целевой пользователь (в консоли - его имя).
     * @throws HumanNotFoundException Послано, если такого персонажа не существует.
     */
    public boolean meals(Connection con, String human) {
        Human object = Human.getHumanByName(human);
        ArrayList<Food> list = object.readMeals();

        con.out.println("Текущее насыщение " + object.getName() + ": " +
                object.getCurrentSaturation() + "/" + object.getMaxSaturation());

        for (Food food: list) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            con.out.println(
                    food.getName() + ":\tнасыщение - " + food.getSaturation() + ", " +
                            (
                                    (!food.isAffecting()) ? "уже не действует." :
                                            ("действует до " + sdf.format(food.getSaturationExpirationDate()))
                            )
            );
        }
        return true;
    }

    /**
     * Попытка съесть пищу с заданными характеристиками.<br>
     * Прием пищи сохраняется в таблицу CSV-объектов и дает доступ
     * к ней через команду meals.<br>
     * <br>
     * Использование команды: eat foodName saturation saturationTime
     * @param foodName Название еды
     * @param saturation Величина, на которую увеличится насыщение персонажа в условных единицах,
     *                   где 100 единиц - это тарелка котлеток с пюрешкой.
     * @param saturationTime Время действия насыщения (в минутах)
     * @throws SaturationException Послано, если персонаж не может съесть такой объем пищи.
     */
    public boolean eat(Connection con, String foodName, int saturation, int saturationTime) {
        Human consumer = Human.getHumanByName( con.getLogUser() );
        Food food = new Food(consumer, foodName, saturation, saturationTime * 60 * 1000);

        if ( consumer.getCurrentSaturation() + food.getSaturation() > consumer.getMaxSaturation() )
            throw new SaturationException(consumer);

        TreeMap<String, Food> map = new TreeMap<>();
        map.put( food.getConsumer().getName(), food );

        FileIOHelper mFileIOHelper = new FileIOHelper();
        mFileIOHelper.writeCSVMapIntoFile(map, true);

        return true;
    }

    /**
     * Просмотр списка всех существующих команд.<br>
     * <br>
     * Использование команды: help
     */
    public boolean help(Connection con) {
        con.out.println("Используйте help [command] для получения описания определенной команды.");
        TreeMap<String, Command> map = Command.getMap();
        for (Map.Entry<String, Command> entry: map.entrySet()) {
            con.out.println(entry.getValue().getName());
        }
        return true;
    }

    /**
     * Просмотр информации о команде.<br>
     * <br>
     * Использование команды: help command
     * @param command Название команды
     * @throws CommandNotFoundException Послано, если такой команды не существует.
     */
    public boolean help(Connection con, String command) {
        con.out.println( Command.getCommandByName(command).getDescription() );
        return true;
    }

    /**
     * Создание персонажа с заданными характеристиками или перезаписывает его, если такой персонаж есть.<br>
     * Грусть персонажа при создании равна 0 (т.е. персонаж создается максимально счастливым).<br>
     * Все персонажи сохраняются в соответствующую коллекцию в формате CSV.<br>
     * <br>
     * Использование команды: human name birthday maxSaturation gender iq
     * @param name Имя персонажа (уникальный ключ).
     * @param birthday Дата рождения (при запуске из консоли - в формате ДД.ММ.ГГГГ).
     * @param maxSaturation Максимальный уровень насыщения в условных единицах,
     *                      где 100 единиц - это тарелка котлеток с пюрешкой.
     * @param gender Гендер персонажа (при запуске из консоли - его название в соответствии с командой gender).
     */
    public boolean human(String name, Date birthday, int maxSaturation, Gender gender) {
        Human human = new Human(name, birthday, maxSaturation, gender);
        return true;
    }

    /**
     * Вывести последовательно содержимое файлов.<br>
     * Используется, в основном, для просмотра логов не выходя из <i>FoodShell</i>.<br>
     * <br>
     * Использование команды: cat file1 file2 ...
     * @param files Путь до файла (относительный или абсолютный)
     */
    public boolean cat(Connection con, String... files) {
        for (String file: files) {
            try {
                FileInputStream fis = new FileInputStream(file);
                Scanner fileInput = new Scanner(fis);
                while (fileInput.hasNextLine())
                    con.out.println(fileInput.nextLine());
            }
            catch(IOException exc) {
                con.out.println("Cannot read file " + file);
            }
        }
        return true;
    }

    /**
     * Выводит список всех (нет) существующих гендеров.<br>
     * <br>
     * Использование команды: gender
     */
    public boolean gender(Connection con) {
        Gender.printPostulate();
        for (Gender gender: Gender.values())
            con.out.println(gender.getName());
        return true;
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
    public boolean insert(String name, JSONObject object) {
        object.put("name", name);
        new Location(object, true);
        return true;
    }

    /**
     * Просмотр списка имен и координат существующих локаций.<br>
     * <br>
     * Использование команды: show
     */
    public boolean show(Connection con) {
        FileIOHelper mFileIOHelper = new FileIOHelper();
        ArrayList<CSVObject> list = mFileIOHelper.readCSVListFromFile( Location.PATH );

        for(CSVObject csv: list) {
            Coordinate coordinate = new Coordinate(
                    csv.getDouble("x"),
                    csv.getDouble("y"),
                    csv.getDouble("z")
            );

            con.out.println(csv.getString("name") + ":\t" + coordinate);
        }
        return true;
    }

    /**
     * Просмотр данных о текущем пользователе, а именно гендер, имя персонажа, его возраст (в годах),
     * уровень текущего насыщения, настроения, его местоположение и точные координаты.<br>
     * <br>
     * Использование команды: me
     */
    public boolean me(Connection con) {
        Human me = Human.getHumanByName(con.getLogUser());
        Coordinate coordinate = me.getLocation().getCoords();

        con.out.println( String.format( Locale.US,

        "%s %s, %d лет.\n" +
                "Насыщение - %d/%d.\n" +
                "Уровень печали - %d.\n" +
                "Местоположение - %s, точные координаты - %s.",

                me.getGender().getName(), me.getName(), me.getAge(),
                me.getCurrentSaturation(), me.getMaxSaturation(),
                me.getSadness(),
                me.getLocation().getName(), coordinate
        ) );
        return true;
    }

    /**
     * Залогиниться за другого пользователя-персонажа.<br>
     * <br>
     * Использование команды: login name
     * @param name Имя персонажа (уникальный ключ).
     * @throws HumanNotFoundException Послано, если такого персонажа не существует.
     */
    public boolean login(Connection con, String name) {
        con.setLogUser(name);
        return true;
    }

    /**
     * Переместить текущего пользователя-персонажа в заданную локацию.<br>
     * <br>
     * Использование команды: move location
     * @param location Название локации (уникальный ключ).
     * @throws LocationNotFoundException Послано, если целевая локация не существует.
     */
    public boolean move(Connection con, String location) {
        Human me = Human.getHumanByName(con.getLogUser());
        Location loc = Location.getLocationByName(location);
        me.setLocation(loc);
        Human.update();

        return true;
    }

    /**
     * Текущий персонаж смеется над заданным персонажем с заданной силой.
     * Причина смеха не определена и остается на фантазию пользователя.<br>
     * В результате грусть текущего пользователя уменьшается на значение силы,
     * грусть получателя же увеличивается.<br>
     * <br>
     * Использование команды: laugh power receiver
     * @param power Сила смеха в условных единицах, где 5 единиц - это рандомный мемный видос.
     * @param receiver Имя получателя (унивальный ключ).
     * @throws HumanNotFoundException Послано, если такого получателя не существует.
     */
    public boolean laugh(Connection con, int power, String receiver) {
        Human me = Human.getHumanByName(con.getLogUser());
        Human hReceiver = Human.getHumanByName(receiver);

        me.setSadness( me.getSadness() - power );
        hReceiver.setSadness( hReceiver.getSadness() + power );
        Human.update();

        if (power <= 5) {
            me.sayPhrase("Ха-ха-ха...");
            return true;
        }

        if (power <= 10) {
            me.sayPhrase("Ахаха!!");
            return true;
        }

        me.sayPhrase("АХАХАХАХАХА");
        return true;
    }

    /**
     * Попытка развеселить заданного персонажа на заданный уровень.<br>
     * Если грусть персонажа-пользователя больше, чем у заданного, поднять ему настроение он не может.
     * В другом случае грусть заданного персонажа уменьшается на значение уровня.<br>
     * <br>
     * Использование команды: cheer level receiver
     * @param level Уровень поднятия настроения в условных единицах, где 5 единиц - это рандомный мемный видос.
     * @param receiver Имя получателя (унивальный ключ).
     * @throws HumanNotFoundException Послано, если такого получателя не существует.
     */
    public boolean cheer(Connection con, int level, String receiver) {
        Human me = Human.getHumanByName(con.getLogUser());
        Human hReceiver = Human.getHumanByName(receiver);

        if (me.getSadness() > hReceiver.getSadness()) {
            con.out.println("Вы слишком грустны, чтобы развеселить " + hReceiver.getName() + " (либо он слишком веселый).");
            return true;
        }

        me.sayPhrase(hReceiver.getName() + ", не грусти");

        hReceiver.setSadness( hReceiver.getSadness() - level );
        me.setSadness( me.getSadness() - level );
        Human.update();

        hReceiver.sayPhrase("Ох как хорошо стало сразу!! Птички поют, радуга светит, грусть стала равна " + hReceiver.getSadness() + "!!!!");

        return true;
    }

    /**
     * Текущий персонаж грустит на заданную величину силы.
     * Причина плача не определена и остается на фантазию пользователя.<br>
     * В результате грусть персонажа увеличивается на эту величину.<br>
     * <br>
     * Использование команды: cry power
     * @param power Сила плача в условных единицах, где 5 единиц - это рандомный видос о бедном песике.
     */
    public boolean cry(Connection con, int power) {
        Human me = Human.getHumanByName(con.getLogUser());
        me.setSadness(me.getSadness() + power);
        Human.update();

        if (me.getSadness() <= 15) {
            me.sayPhrase("*вздыхает*");
            return true;
        }

        if (me.getSadness() <= 30) {
            me.sayPhrase("*плачет*");
            return true;
        }

        me.sayPhrase("*орет от безысходности*");
        return true;
    }

    /**
     * Уничтожение локации по ее ключу. Все персонажи, которые были в этой локации, перемещаются в локацию World.<br>
     * Локацию World уничтожить нельзя (появляется соответствующее сообщение).<br>
     * <br>
     * Использование команды: remove name
     * @param name Название локации (уникальный ключ).
     * @throws LocationNotFoundException Послано, если такой локации не существует.
     */
    public boolean remove(Connection con, String name) {
        TreeMap<String, Location> map = Location.getMap();

        if (name.equals("World")) {
            con.out.println("World невозможно уничтожить (по крайней мере, тебе)");
            return true;
        }

        map.remove( name );
        Location.setMap(map);

        Location.update();
        Human.update();

        return true;
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
    public boolean remove_greater(JSONObject object) {
        double height = object.getDouble("y");
        TreeMap<String, Location> map = Location.getMap();

        Iterator< Map.Entry<String, Location> > iterator = map.entrySet().iterator();
        while ( iterator.hasNext() ) {
            Map.Entry<String, Location> entry = iterator.next();
            Location temp = entry.getValue();
            if (temp.getCoords().getY() > height && !temp.getName().equals("World")) {
                iterator.remove();
            }
        }
        Location.setMap(map);

        Location.update();
        Human.update();

        return true;
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
    public boolean remove_lower(JSONObject object) {
        double height = object.getDouble("y");
        TreeMap<String, Location> map = Location.getMap();

        Iterator< Map.Entry<String, Location> > iterator = map.entrySet().iterator();
        while ( iterator.hasNext() ) {
            Map.Entry<String, Location> entry = iterator.next();
            Location temp = entry.getValue();
            if (temp.getCoords().getY() < height && !temp.getName().equals("World")) {
                iterator.remove();
            }
        }
        Location.setMap(map);

        Location.update();
        Human.update();

        return true;
    }

    /**
     * Очистка коллекции с локациями a.k.a. уничтожение всех локаций, кроме World.<br>
     * При этом время создания локации не перезаписывается.<br>
     * <br>
     * Использование команды: clear
     */
    public boolean clear() {
        Location.setMap( new TreeMap<>() );
        new Location();

        Location.update();
        Human.update();

        return true;
    }

    /**
     * Получение информации о состоянии текущей коллекции a.k.a. состоянии текущего мира.<br>
     * Информация: дата создания мира, тип коллекции, количество локаций.<br>
     * <br>
     * Использование команды: info
     */
    public boolean info(Connection con) {
        try {
            File file = new File(Location.PATH);
            BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String creationDate = sdf.format(attributes.creationTime().toMillis());
            con.out.println("Дата создания: " + creationDate);
        }
        catch (IOException exc) {
            con.out.println("Невозможно определить дату создания.");
        }

        FileIOHelper mFileIOHelper = new FileIOHelper();
        int mapSize = mFileIOHelper.readCSVListFromFile(Location.PATH).size();

        con.out.println("Тип коллекции: соответствие в виде дерева существующих локаций и их имен.\n" +
                "Размер коллекции: " + mapSize );
        return true;
    }
}
