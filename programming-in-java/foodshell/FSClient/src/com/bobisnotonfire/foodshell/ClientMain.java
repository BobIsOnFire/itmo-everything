package com.bobisnotonfire.foodshell;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        String path = "";

        if (args.length != 0) {
            path = args[0];
        }

        connect(path);
    }

    private static void connect(String path) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите IP и порт в формате XXX.XXX.XXX.XXX:PPPPP\n> ");
        String[] temp = scanner.nextLine().split(":");

        String ip = temp[0];
        int port = Integer.parseInt(temp[1]);

        System.out.print("Введите ваш логин в сети:\n> ");
        String name = scanner.nextLine();

        ClientHelper client = new ClientHelper(ip, port, name, path);

        Thread receiver = client.receiveOutput();

        String str;
        while (!client.receiverStopped(receiver)) {
            if (!scanner.hasNextLine() || ( str = scanner.nextLine() ).trim().equals("exit")) {
                client.uploadCommand("exit");
                break;
            }
            client.uploadCommand(str);
        }

        if (client.receiverStopped(receiver)) {
            System.out.println("Connection lost. Reconnecting? y/n");
            if (scanner.next().equals("y"))
                connect(path);
        }
        else
            client.stopReceiver(receiver);
    }
}
