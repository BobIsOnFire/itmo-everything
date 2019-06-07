package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.guiframe.AuthorizeFrame;
import com.bobisonfire.foodshell.guiframe.MainFrame;

import javax.swing.*;

public class Main {
    private static final String FS_VERSION = "7.0.0";
    private static final String IP = "localhost";
    private static final int PORT = 31678;

    public static void main(String[] args) {
        JFrame aframe = new AuthorizeFrame("Авторизация");
        aframe.setVisible(true);

        JFrame frame = new MainFrame("FoodShell v." + FS_VERSION);
        frame.setVisible(true);
    }
}
