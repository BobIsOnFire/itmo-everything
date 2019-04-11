package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.entity.Location;
import com.bobisonfire.foodshell.exc.NotFoundException;
import com.bobisonfire.foodshell.exc.SaturationException;
import com.bobisonfire.foodshell.exc.TransformerException;
import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.transformer.CSVObject;

import java.io.*;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * FoodShell - основной класс для работы со средой <i>FoodShell</i>.
 */
public class Connection extends Thread {
    public String getLogUser() {
        return logUser;
    }

    public void setLogUser(String logUser) {
        Human.getHumanByName(logUser);
        this.logUser = logUser;
    }

    private BufferedReader in;
    public PrintWriter out;
    private Socket socket;

    private String connectionName;
    private Path path;
    private String logUser = "God";


    public void run() {
        try {
            connectionName = in.readLine();
            String pathString = in.readLine();

            if (pathString.equals(""))
                pathString = Location.PATH;

            path = Paths.get(pathString);

            System.out.println(connectionName + " вошел. Использует коллекцию по адресу " + path.toAbsolutePath());
            out.println("Использую коллекцию по адресу " + path.toAbsolutePath() + ". Для изменения воспользуйтесь " +
                    "командами import/load");

            initializeMessage();
            while (true) {
                String[] tokens = readCommand();
                if (tokens.length == 0)
                    continue;
                if (tokens[0].equals("exit")) {
                    out.println("Работа закончена");
                    break;
                }

                try {
                    Command command = Command.getCommandByName(tokens[0]);
                    command.launch(this, Arrays.copyOfRange(tokens, 1, tokens.length));
                } catch (TransformerException exc) {
                    out.println("Неверный формат заданного объекта.");
                } catch (NumberFormatException exc) { // caught if non-numeric words are given as numeric arguments
                    out.println("Часть аргументов не являются числами нужного формата.");
                } catch (SaturationException | NotFoundException exc) {
                    out.println(exc.getMessage());
                } catch (IndexOutOfBoundsException exc) { // caught if number of args in console command is insufficient
                    out.println("Неверный вызов команды.");
                }
            }

            System.out.println(connectionName + " вышел.");
        } catch(Exception e) {
            Main.logException(e);
        }
    }

    /**
     * Метод, запускающий среду <i>FoodShell</i>. Выполняет следующие функции:<br>
     * 1. Считывает из консоли путь до файла, содержащего локации и останавливает <i>FoodShell</i>,
     * если путь не введен.<br>
     * 2. Синхронизирует коллекции локаций и людей, заполняя их из соответствующих файлов или записывая
     * в файлы коллекции по умолчанию, если файлы пусты.<br>
     * 3. Организовывает чтение команд, их исполнение и обработку неверно вызванных команд.<br>
     * 4. Логирует критические ошибки среды <i>FoodShell</i>, останавливая ее выполнение.
     */
    public Connection(Socket socket) {
        this.socket = socket;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch(IOException e) {
            e.printStackTrace(); // todo логировать по-другому
        }


        FileIOHelper mFileIOHelper = new FileIOHelper();

        List<CSVObject> CSVList = mFileIOHelper.readCSVListFromFile(Location.PATH);
        if (CSVList.size() == 0)
            new Location(); // todo не хранить в памяти джавы в принципе
        for (CSVObject csv: CSVList) {
            new Location( csv, false );
        }

        CSVList = mFileIOHelper.readCSVListFromFile(Human.PATH);
        if (CSVList.size() == 0)
            new Human();
        for (CSVObject csv: CSVList) {
            new Human( csv, false );
        }
    }

    /**
     * Метод, организовывающий чтение и токенизацию команд.<br>
     * Интерпретатор считает токеном структуру, разделенную любым количеством пробелов,
     * не включенных в кавычки или фигурные скобки.<br>
     * Пример: структуру "I did not hit her" он воспримет как единый токен, без кавычек -
     * как 5 отдельных токенов. Аналогично с фигурными скобками.
     * @return Массив токенов из введенной строки.
     */
    private String[] readCommand() {
        printInvitation();
        Scanner standardScanner = new Scanner(in);
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

    private void initializeMessage() {
        out.println("FoodShell v" + Main.VERSION + ". Some rights reserved.");
        out.println("Введите help для списка всех команд.\n");
    }

    private void printInvitation() {
        out.write(logUser + "@FoodShell> ");
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            Main.logException(e);
        }

        Main.server.removeConnection(this);
    }
}
