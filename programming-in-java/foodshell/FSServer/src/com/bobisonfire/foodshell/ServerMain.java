package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.entity.Location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * Класс, отвецающий за запуск серверной части <i>FoodShell</i>.<br>
 * <i>FoodShell</i> - интерактивное консольное многопользовательское приложение,
 * в котором каждый пользователь управляет группой персонажей - он может их создавать,
 * удалять и перемещать в пространстве. Перемещение осуществляется закреплением
 * персонажа к определенной локации. Изначально существуют персонаж God и локация World.<br>
 * Структура <i>FoodShell</i>:<br>
 * 1. com.bobisonfire.foodshell - организовывает работу приложения с внешними ресурсами:
 * файлами с CSV-таблицами и клиентской частью приложения;<br>
 * 2. com.bobisonfire.foodshell.entity - хранит классы, необходимые для создания сериализуемых
 * объектов: интерфейс и классы самих объектов и классы, являющиеся полями этих объектов;<br>
 * 3. com.bobisonfire.foodshell.transformer - хранит классы, использующиеся для десериализации;<br>
 * 4. com.bobisonfire.foodshell.commands - организовывает логику исполнения консольных команд;<br>
 * 5. com.bobisonfire.foodshell.exc - хранит пользовательские исключения, использующиеся в
 * <i>FoodShell</i>.
 * @author BobIsOnFire - Nikita Akatyev: Programming Lab6 2019
 * @version 5.2.5
 */
public class ServerMain {
    private static final String PATH_PREFIX = ""; private static final boolean debug = true;
//    private static final String PATH_PREFIX = "/home/s264443/prog/lab6/"; private static final boolean debug = false;
    private static final String ERROR_PATH = PATH_PREFIX + "error.log";

    public static final String VERSION = "5.2.5";
    public static ServerHelper server;

    public static void main(String[] args) {
        if (debug)
            System.out.println("Running debug version..");

        if (args.length > 0)
            Human.PATH = args[0];
        else
            Human.PATH = PATH_PREFIX + Human.PATH;

        try {
            initializeMessage();

            Location.PATH = PATH_PREFIX + Location.PATH;
            Command.createBasicCommands();

            if (!new File(Human.PATH).exists())
                Files.write(Paths.get(Human.PATH), Collections.singleton( new Human().toCSV() ));

            if (!new File(Location.PATH).exists())
                Files.write(Paths.get(Location.PATH), Collections.singleton( new Location().toCSV() ));


            server = new ServerHelper();
            server.runServer();
        } catch (Exception e) {
            logException(e);
        }
    }

    /**
     * Сохраняет информацию о возникшей ошибке в лог ошибок (по умолчанию - error.log в папке с лабой).
     */
    static void logException(Exception exc) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        try {
            FileWriter fw = new FileWriter(ERROR_PATH, true);
            BufferedWriter writer = new BufferedWriter(fw);

            writer.write( sdf.format(new Date()) + ": " + exc.getClass().getName() + ": " + exc.getMessage() + "\n" );

            for (StackTraceElement stack : exc.getStackTrace())
                writer.write("\t" + stack + "\n");

            writer.write('\n');
            writer.close();
        }
        catch (IOException e) {
            System.out.println("Ошибка! Не смог создать лог для ошибок. Ошибка была, но не залогировалась, держу в курсе.");
        }
    }

    private static void initializeMessage() {
        System.out.println("FoodShellServer v" + VERSION + ". Some rights reserved.");
    }
}
