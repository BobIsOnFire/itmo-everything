package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.entity.Location;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

/**
 * Класс, отвецающий за запуск серверной части приложения.
 */
public class ServerMain {
    private static final String PATH_PREFIX = "";
//    private static final String PATH_PREFIX = "/home/s264443/prog/lab6/";
    private static final String ERROR_PATH = PATH_PREFIX + "error.log";
    private static final FileIOHelper f = new FileIOHelper();

    public static final String VERSION = "5.2.3";
    public static ServerHelper server;

    public static void main(String[] args) {

        if (args.length > 0)
            Human.PATH = args[0];
        else
            Human.PATH = PATH_PREFIX + Human.PATH;

        try {
            initializeMessage();

            Location.PATH = PATH_PREFIX + Location.PATH;
            Command.createBasicCommands();

            if (!new File(Human.PATH).exists())
                f.writeCSVSetIntoFile(Collections.singleton(new Human()), Human.PATH);

            if (!new File(Location.PATH).exists())
                f.writeCSVSetIntoFile(Collections.singleton(new Location()), Location.PATH);


            server = new ServerHelper();
            server.runServer();
        } catch (Exception e) {
            logException(e);
        }
    }

    /**
     * Сохраняет информацию о возникшей ошибке в лог ошибок (по умолчанию - error.log в корневой папке).
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
