package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.entity.Food;
import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.entity.Location;
import com.bobisonfire.foodshell.exc.NotFoundException;
import com.bobisonfire.foodshell.exc.SaturationException;
import com.bobisonfire.foodshell.exc.TransformerException;
import com.bobisonfire.foodshell.transformer.CSVObject;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class FoodShell {
    private static final String PATH_PREFIX = "";
    private static final String ERROR_PATH = PATH_PREFIX + "error.log";
    private static String logUser = "God";
    private static final String VERSION = "4.0.1";

    public static String getLogUser() {
        return logUser;
    }

    public static void setLogUser(String logUser) {
        Human.getHumanByName(logUser);
        FoodShell.logUser = logUser;
    }

    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Введите путь до файла, содержащего ваши локации.");
            return;
        }
        Location.PATH = args[0];

        Human.PATH = PATH_PREFIX + Human.PATH;
        Food.PATH = PATH_PREFIX + Food.PATH;

        FileIOHelper mFileIOHelper = new FileIOHelper();

        ArrayList<CSVObject> CSVList = mFileIOHelper.readCSVListFromFile(Location.PATH);
        if (CSVList.size() == 0)
            new Location();
        for (CSVObject csv: CSVList) {
            new Location( csv, false );
        }

        CSVList = mFileIOHelper.readCSVListFromFile(Human.PATH);
        if (CSVList.size() == 0)
            new Human();
        for (CSVObject csv: CSVList) {
            new Human( csv, false );
        }

        Command.createBasicCommands();

        boolean nextCommand = true;

        try {
            initializeMessage();
            while (nextCommand) {
                String[] tokens = readCommand();
                if (tokens.length == 0)
                    continue;

                try {
                    Command command = Command.getCommandByName(tokens[0]);
                    nextCommand = command.launch(Arrays.copyOfRange(tokens, 1, tokens.length));
                } catch (TransformerException exc) {
                    System.out.println("Неверный формат заданного объекта.");
                } catch (NumberFormatException exc) { // caught if non-numeric words are given as numeric arguments
                    System.out.println("Часть аргументов не являются числами нужного формата.");
                } catch (SaturationException | NotFoundException exc) {
                    System.out.println(exc.getMessage());
                } catch (IndexOutOfBoundsException exc) { // caught if number of args in console command is insufficient
                    System.out.println("Неверный вызов команды.");
                }
            }
        }
        catch (Exception exc) {
            logException(exc);
        }
    }

    private static String[] readCommand() {
        printInvitation();
        Scanner standardScanner = new Scanner(System.in);
        if (!standardScanner.hasNextLine())
            return new String[0];

        String[] tokens = standardScanner.nextLine().split("\\s+");

        int quoteCounter = 0;
        int bracketCounter = 0;
        boolean tokenComplete = true;
        ArrayList<String> result = new ArrayList<>();

        for (String token: tokens) {
            if (token.equals(""))
                continue;

            quoteCounter += token.chars().filter(ch -> ch == '"').count();

            bracketCounter += token.chars().filter(ch -> ch == '{').count()
                            - token.chars().filter(ch -> ch == '}').count();

            String temp = token.replaceAll("\"", "");
            if (tokenComplete)
                result.add(temp);
            else {
                int index = result.size() - 1;
                result.set(index, result.get(index) + " " + temp);
            }

            tokenComplete = quoteCounter % 2 == 0 && bracketCounter == 0;
        }

        return result.toArray(new String[0]);
    }

    private static void initializeMessage() {
        System.out.println("FoodShell v" + VERSION + ". Some rights reserved.");
        System.out.println("Введите help для списка всех команд.\n");
    }

    private static void printInvitation() {
        System.out.print(logUser + "@FoodShell> ");
    }

    private static void logException(Exception exc) {
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
}
