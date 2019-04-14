package com.bobisnotonfire.foodshell;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        String path = null;

        if (args.length != 0) {
            path = args[0];
        }

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите IP и порт в формате XXX.XXX.XXX.XXX:PPPPP\n> ");
        String[] temp = scanner.nextLine().split(":");

        String ip = temp[0];
        int port = Integer.parseInt(temp[1]);

        System.out.print("Введите ваш логин в сети:\n> ");
        String name = scanner.nextLine();

        ClientHelper client = new ClientHelper(ip, port, name, path);

        Thread receiver = client.receiveOutput();

        String str = "";
        while (!str.equals("exit") && scanner.hasNextLine()) {
            str = scanner.nextLine();
            client.uploadCommand(str);
        }

        client.stopReceiver(receiver);
    }
}
