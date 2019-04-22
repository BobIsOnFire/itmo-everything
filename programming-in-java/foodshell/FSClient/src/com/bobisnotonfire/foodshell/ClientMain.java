package com.bobisnotonfire.foodshell;

import java.io.IOException;
import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        connect();
    }

    private static void connect() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите IP и порт в формате HOST:PORT\n> ");
        String[] temp = new String[0];
        boolean addressCorrect = false;
        while (!addressCorrect) {
            if (!scanner.hasNextLine())
                System.exit(0);
            temp = scanner.nextLine().split(":");

            addressCorrect = temp.length == 2 &&
                    temp[1].matches("^\\d+$") &&
                    Integer.parseInt(temp[1]) > 10000 &&
                    Integer.parseInt(temp[1]) < 65536;

            if (!addressCorrect)
                System.out.print("Адрес некорректен. Введите еще раз.\n> ");
        }

        String ip = temp[0];
        int port = Integer.parseInt(temp[1]);

        System.out.print("Введите ваш логин в сети:\n> ");
        String name = "";
        while ( name.equals("") ) {
            name = scanner.nextLine();

            if (name.equals(""))
                System.out.println("Введите непустое имя.");
        }

        ClientHelper client = new ClientHelper(ip, port, name);

        Thread receiver = client.receiveOutput();

        String str;
        while (!client.receiverStopped(receiver)) {
            if (!scanner.hasNextLine() || (str = scanner.nextLine()).trim().equals("exit")) {
                client.uploadCommand("exit");
                break;
            }
            client.uploadCommand(str);

            if (str.trim().startsWith("export ")) {
                String p = str.trim().split("\\s+")[1];
                client.exportFile(p);
            }
        }

        if (client.receiverStopped(receiver)) {
            System.out.println("Соединение потеряно. Попробовать новое соединение? y/n");
            if (scanner.hasNextLine() && scanner.next().equals("y")) {
                client.close();
                connect();
            }
        }
        else
            client.stopReceiver(receiver);
    }
}
