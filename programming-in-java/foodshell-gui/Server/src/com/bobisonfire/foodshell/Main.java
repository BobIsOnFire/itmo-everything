package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.exchange.DBExchanger;
import com.bobisonfire.foodshell.exchange.MailSender;
import com.bobisonfire.foodshell.exchange.Request;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Main {
    private static final String FS_VERSION = "7.0.0";

    private static final int PORT = 31678;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            String username, password;

            System.out.print("Введите данные для входа на внешние ресурсы:" + "\n" +
                    "Почта" + "\n\t" + "Логин: ");
            username = scanner.nextLine();
            System.out.print("\t" + "Пароль: ");
            password = new String( System.console().readPassword() );
            MailSender.setCredentials(username, password);

            System.out.print("База данных" + "\n\t" + "Логин: ");
            username = scanner.nextLine();
            System.out.print("\t" + "Пароль: ");
            password = new String( System.console().readPassword() );
            DBExchanger.setCredentials(username, password);
        } catch (Exception exc) {
            System.exit(0);
        }

        System.out.println("\nFoodShellServer v." + FS_VERSION + ". Some rights reserved.");

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
                        SocketChannel socketChannel = (SocketChannel) key.channel();

                        ByteBuffer buffer = ByteBuffer.allocate(256);
                        if (socketChannel.read(buffer) < 0) socketChannel.socket().close(); // todo log connections

                        buffer.flip();
                        byte[] bytes = new byte[buffer.limit()];
                        buffer.get(bytes);
                        String message = new String(bytes);

                        Request.execute(message, socketChannel);
                    })
                    .run(PORT);
        } catch (ServerException exc) {
            // todo logging
        }
    }
}
