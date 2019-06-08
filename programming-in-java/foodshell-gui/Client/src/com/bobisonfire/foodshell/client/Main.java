package com.bobisonfire.foodshell.client;

import com.bobisonfire.foodshell.client.exchange.Request;
import com.bobisonfire.foodshell.client.guiframe.AuthorizeFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;

public class Main {
    public static final String FS_VERSION = "7.0.0";
    private static final String IP = "localhost";
    private static final int PORT = 31678;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(IP, PORT);
            Request.setSocket(socket);

            JFrame frame = new AuthorizeFrame("Авторизация");
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
