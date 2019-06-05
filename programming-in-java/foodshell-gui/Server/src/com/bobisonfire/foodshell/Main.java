package com.bobisonfire.foodshell;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    private static final String PATH_PREFIX = "";
    private static final String FS_VERSION = "7.0.0";

    private static final int PORT = 31678;

    private static String EMAIL_LOGIN;
    private static String EMAIL_PASSWORD;
    private static String DB_LOGIN;
    private static String DB_PASSWORD;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Введите данные для входа на внешние ресурсы:" + "\n" +
                    "Почта" + "\n\t" + "Логин: ");
            EMAIL_LOGIN = scanner.nextLine();
            System.out.print("\t" + "Пароль: ");
            EMAIL_PASSWORD = new String( System.console().readPassword() );
            System.out.print("База данных" + "\n\t" + "Логин: ");
            DB_LOGIN = scanner.nextLine();
            System.out.print("\t" + "Пароль: ");
            DB_PASSWORD = new String( System.console().readPassword() );
        } catch (Exception exc) {
            System.exit(0);
        }

        try {
            TCPServerRunner.instance()
                    .setOnAccept(key -> {
                        SocketChannel socketChannel = ( (ServerSocketChannel) key.channel() ).accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(key.selector(), SelectionKey.OP_READ);
                        System.out.printf("Получено соединение от %s:%d",
                                socketChannel.socket().getInetAddress(), socketChannel.socket().getPort());
                    })
                    .setOnRead(key -> {
                        // todo client request handling
                    })
                    .run(PORT);
        } catch (ServerException exc) {
            // todo logging
        }
    }
}
