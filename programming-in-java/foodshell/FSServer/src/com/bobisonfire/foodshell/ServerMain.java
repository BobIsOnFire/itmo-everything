package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.entity.Location;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerMain {
    private static final String PATH_PREFIX = "";
//    private static final String PATH_PREFIX = "/home/s264443/prog/lab6/";
    private static final String ERROR_PATH = PATH_PREFIX + "error.log";
    public static final String VERSION = "5.1.3";
    public static ServerHelper server;

    public static void main(String[] args) {
        initializeMessage();

        // todo сменить обозначения - пусть персональной будет коллекция человеков
        Location.PATH = PATH_PREFIX + Location.PATH;
        Human.PATH = PATH_PREFIX + Human.PATH;

        Command.createBasicCommands();

        server = new ServerHelper();
        server.runServer();
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
