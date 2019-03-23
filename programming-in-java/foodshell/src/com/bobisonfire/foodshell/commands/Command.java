package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.entity.Gender;
import com.bobisonfire.foodshell.exc.CommandNotFoundException;
import com.bobisonfire.foodshell.transformer.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TreeMap;
import java.util.Date;

/**
 * Класс, реализующий логику исполняемых команд.<br>
 */
public class Command {
    private String name;
    private String description;
    private Launchable function;
    private static TreeMap<String, Command> CommandMap = new TreeMap<>();

    public Command(String name, String description, Launchable function) {
        this.name = name;
        this.description = description;
        this.function = function;

        CommandMap.put(name, this);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public static Command getCommandByName(String name) {
        if (!CommandMap.containsKey(name))
            throw new CommandNotFoundException(name);
        return CommandMap.get(name.intern());
    }

    public static TreeMap<String, Command> getMap() {
        return CommandMap;
    }



    public boolean launch(String[] tokens) {
        return function.start(tokens);
    }

    /**
     * Метод, запускаемый при инициализации <i>FoodShell</i>, соединяющий логику исполнения команд
     * (см. CommandDoc) с логикой самой команды.
     */
    public static void createBasicCommands() {
        final CommandDoc launcher = new CommandDoc();
        new Command(
                "exit",
                "exit - завершить работу с FoodShell.",
                tokens -> launcher.exit()
        );

        new Command(
                "meals",
                "meals [human] - история всех приемов пищи текущего пользователя [или человека human] и его текущий уровень насыщения.",
                tokens -> {
                    if (tokens.length == 0)
                        return launcher.meals();
                    return launcher.meals(tokens[0]);
                }
        );

        new Command(
                "eat",
                "eat foodName saturation saturationTime - попытка съесть пищу со следующими характеристиками:\n" +
                        "\tfoodName:\tназвание;\n" +
                        "\tsaturation:\tнасыщение в условных единицах (из расчета, что 100 единиц - это тарелка котлеток с пюрешкой);\n" +
                        "\tsaturationTime:\tвремя, в течение которого будет действовать насыщение (в минутах).",
                tokens -> launcher.eat( tokens[0], Integer.parseInt(tokens[1]), Long.parseLong(tokens[2]) )
        );

        new Command(
                "help",
                "help [command] - показывает список всех существующих команд [либо описание команды command]",
                tokens -> {
                    if (tokens.length == 0) {
                        return launcher.help();
                    } else return launcher.help(tokens[0]);

                }
        );

        new Command(
                "human",
                "human name birthday maxSaturation gender - создать нового человека со следующими характеристиками:\n" +
                        "\tname:\tимя существа;\n" +
                        "\tbirthday:\tдень рождения в формате ДД.ММ.ГГГГ;\n" +
                        "\tmaxSaturation:\tмаксимальное количество еды, которое он может съесть;\n" +
                        "\tgender:\tего гендер (команда gender выведет список всех гендеров), многословные гендеры берутся в кавычки.",
                tokens -> {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    Date birthday = new Date();
                    try {
                        birthday = sdf.parse(tokens[1]);
                    }
                    catch (ParseException exc) {
                        System.out.println("Неверный формат даты! Ставлю сегодняшнюю..");
                    }

                    Gender gender = Gender.getGenderByName(tokens[3]);

                    return launcher.human(tokens[0], birthday, Integer.parseInt(tokens[2]), gender);
                }
        );

        new Command(
                "cat",
                "cat file1 [file2 ...] - вывести последовательно содержимое файлов.",
                launcher::cat
        );

        new Command(
                "gender",
                "gender - выводит список всех (нет) существующих гендеров.",
                tokens -> launcher.gender()
        );

        new Command(
                "insert",
                "insert name {object} - создание новой локации со следующими характеристиками:\n" +
                        "\tname:\tназвание локации;\n" +
                        "\t{object}:\tjson-объект, содержащий поля 'x','y','z' с координатами локации (остальные поля игнорируются).",
                tokens -> launcher.insert(tokens[0], new JSONObject(tokens[1]))
        );

        new Command(
                "show",
                "show - список имен и координат существующих локаций.\n",
                tokens -> launcher.show()
        );

        new Command(
                "me",
                "me - данные о текущем пользователе.",
                tokens -> launcher.me()
        );

        new Command(
                "login",
                "login name - войти как другой пользователь name.",
                tokens -> launcher.login(tokens[0])
        );

        new Command(
                "move",
                "move location - переместиться в локацию location.",
                tokens -> launcher.move(tokens[0])
        );

        new Command(
                "laugh",
                "laugh power receiver - посмеяться над receiver с силой power.",
                tokens -> launcher.laugh( Integer.parseInt(tokens[0]), tokens[1] )
        );

        new Command(
                "cheer",
                "cheer level receiver - попробовать развеселить receiver на уровень level.",
                tokens -> launcher.cheer( Integer.parseInt(tokens[0]), tokens[1] )
        );

        new Command(
                "cry",
                "cry power - персонаж плачет с силой power.",
                tokens -> launcher.cry( Integer.parseInt(tokens[0]) )
        );

        new Command(
                "remove",
                "remove name - уничтожить локацию name.",
                tokens -> launcher.remove(tokens[0])
        );

        new Command(
                "remove_greater",
                "remove_greater {object} - уничтожить все локации, находящиеся выше чем json-объект (обязательно наличие поля 'y').",
                tokens -> launcher.remove_greater(new JSONObject(tokens[0]))
        );

        new Command(
                "remove_lower",
                "remove_lower {object} - уничтожить все локации, находящиеся ниже чем json-объект (обязательно наличие поля 'y').",
                tokens -> launcher.remove_lower(new JSONObject(tokens[0]))
        );

        new Command(
                "clear",
                "clear - уничтожить все локации (кроме локации World, разумеется).",
                tokens -> launcher.clear()
        );

        new Command(
                "info",
                "info - получить информацию о текущей коллекции.",
                tokens -> launcher.info()
        );
    }
}
