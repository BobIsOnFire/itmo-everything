package com.bobisonfire.foodshell.client;

import com.bobisonfire.foodshell.client.exchange.Request;
import com.bobisonfire.foodshell.client.guiframe.AuthorizeFrame;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main {
    public static final String FS_VERSION = "7.0.0";
    public static ResourceBundle R;

    private static final String IP = "localhost";
    private static final int PORT = 31678;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(IP, PORT);
            Request.setSocket(socket);

            R = ResourceBundle.getBundle("strings", Locale.forLanguageTag("ru-RU"));
            JFrame frame = new AuthorizeFrame( R.getString("authorize") );
            frame.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
