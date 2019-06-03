package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Класс, отвецающий за запуск серверной части <i>FoodShell</i>.<br>
 * <i>FoodShell</i> - интерактивное консольное многопользовательское приложение,
 * в котором каждый пользователь управляет группой персонажей - он может их создавать,
 * удалять и перемещать в пространстве. Перемещение осуществляется закреплением
 * персонажа к определенной локации. Изначально существуют персонаж God и локация World.<br>
 * Структура <i>FoodShell</i>:<br>
 * 1. com.bobisonfire.foodshell - организовывает работу приложения и взаимодействие с внешними ресурсами;<br>
 * 2. com.bobisonfire.foodshell.entity - классы локаций и персонажей, а также вспомогательные для них;<br>
 * 3. com.bobisonfire.foodshell.transformer - классы, использующиеся для десериализации;<br>
 * 4. com.bobisonfire.foodshell.commands - организовывает логику исполнения консольных команд;<br>
 * 5. com.bobisonfire.foodshell.exc - хранит пользовательские исключения, использующиеся в <i>FoodShell</i>.
 * @author BobIsOnFire - Nikita Akatyev: Programming Lab6 2019
 * @version 6.1.3
 */
public class ServerMain {
//    private static final String PATH_PREFIX = ""; static final boolean debug = true;
    private static final String PATH_PREFIX = "/home/s264443/prog/lab7/"; static final boolean debug = false;
    private static final String ERROR_PATH = PATH_PREFIX + "error.log";

    static final String VERSION = "6.1.3";
    static final boolean mailWorking = false;

    static String dbLogin = "";
    static String dbPassword = "";
    static String mailLogin = "";
    static String mailPassword = "";

    public static void main(String[] args) {
        if (debug)
            System.out.println("Running debug version..");

        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Введите данные для входа во внешние системы:\nПочта\n\tЛогин: ");
            mailLogin = sc.nextLine();
            System.out.print("\tПароль: ");
            mailPassword = new String(System.console().readPassword());
            System.out.print("База данных\n\tЛогин: ");
            dbLogin = sc.nextLine();
            System.out.print("\tПароль: ");
            dbPassword = new String(System.console().readPassword());
        } catch (Exception exc) {
            System.exit(0);
        }

        try {
            initializeMessage();
            Command.createBasicCommands();
            ServerHelper server = new ServerHelper();
            server.runServer();
        } catch (Exception e) {
            logException(e);
        }
    }

    /**
     * Сохраняет информацию о возникшей ошибке в лог ошибок (по умолчанию - error.log в папке с лабой).
     */
    public static void logException(Exception exc) {
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
