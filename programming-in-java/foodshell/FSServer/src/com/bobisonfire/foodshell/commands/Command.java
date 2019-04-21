package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.entity.Coordinate;
import com.bobisonfire.foodshell.exc.NotFoundException;
import com.bobisonfire.foodshell.transformer.JSONObject;

import java.util.TreeMap;

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
            throw new NotFoundException("Команда не найдена: " + name);
        return CommandMap.get(name.intern());
    }

    public static TreeMap<String, Command> getMap() {
        return CommandMap;
    }



    public void launch(CommandDoc launcher, String[] tokens) {
        function.start(launcher, tokens);
    }

    /**
     * Метод, запускаемый при инициализации <i>FoodShell</i>, соединяющий логику исполнения команд
     * (см. CommandDoc) с логикой самой команды.
     */
    public static void createBasicCommands() {
        new Command(
                "exit",
                "exit - завершить работу с FoodShell.",
                (launcher, tokens) -> launcher.exit()
        );

        new Command(
                "help",
                "help [command] - показывает список всех существующих команд [либо описание команды command]",
                (launcher, tokens) -> {
                    if (tokens.length == 0) {
                        launcher.help();
                    } else launcher.help(tokens[0]);

                }
        );

        new Command(
                "location",
                "location name x y z - создать новую локацию со следующими характеристиками:\n" +
                        "\tname:\tназвание локации;\n" +
                        "\tx y z:\tкоординаты локации.",
                (launcher, tokens) -> {
                    double x = Double.parseDouble(tokens[1]);
                    double y = Double.parseDouble(tokens[2]);
                    double z = Double.parseDouble(tokens[3]);


                    launcher.location(tokens[0], new Coordinate(x, y, z));
                }
        );

        new Command(
                "locations",
                "locations - просмотр всех существующих локаций.",
                (launcher, tokens) -> launcher.locations()
        );

        new Command(
                "gender",
                "gender - выводит список всех (нет) существующих гендеров.",
                (launcher, tokens) -> launcher.gender()
        );

        new Command(
                "insert",
                "insert name {object} - создание нового персонажа со следующими характеристиками:\n" +
                        "\tname:\tимя персонажа;\n" +
                        "\t{object}:\tjson-объект, описывающий характеристики персонажа (см. документацию).",
                (launcher, tokens) -> launcher.insert(tokens[0], new JSONObject(tokens[1]))
        );

        new Command(
                "show",
                "show - список имен и характеристик существующих персонажей.\n",
                (launcher, tokens) -> launcher.show()
        );

        new Command(
                "me",
                "me - данные о текущем пользователе.",
                (launcher, tokens) -> launcher.me()
        );

        new Command(
                "login",
                "login name - войти как другой пользователь name.",
                (launcher, tokens) -> launcher.login(tokens[0])
        );

        new Command(
                "move",
                "move location - переместиться в локацию location.",
                (launcher, tokens) -> launcher.move(tokens[0])
        );

        new Command(
                "remove",
                "remove name - уничтожить персонажа name.",
                (launcher, tokens) -> launcher.remove(tokens[0])
        );

        new Command(
                "remove_older",
                "remove_older {object} - уничтожить всех персонажей, которые старше чем object (обяязательно наличие поля 'birthday').",
                (launcher, tokens) -> launcher.remove_older(new JSONObject(tokens[0]))
        );

        new Command(
                "remove_younger",
                "remove_younger {object} - уничтожить всех персонажей, которые моложе чем object (обяязательно наличие поля 'birthday').",
                (launcher, tokens) -> launcher.remove_younger(new JSONObject(tokens[0]))
        );

        new Command(
                "clear",
                "clear - уничтожить всех персонажей (кроме God, разумеется).",
                (launcher, tokens) -> launcher.clear()
        );

        new Command(
                "info",
                "info - получить информацию о текущей коллекции.",
                (launcher, tokens) -> launcher.info()
        );

        new Command(
                "import",
                "import path - сменить адрес, по которому находится коллекция.",
                (launcher, tokens) -> launcher._import(tokens[0])
        );
    }
}
