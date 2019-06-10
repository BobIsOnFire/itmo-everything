package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.entities.Human;
import com.bobisonfire.foodshell.client.entities.Location;
import com.bobisonfire.foodshell.client.entities.User;
import com.bobisonfire.foodshell.client.exchange.Request;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class MainFrame extends JFrame {
    static User user;
    static List<Human> humanList = new ArrayList<>();
    static List<Location> locationList = new ArrayList<>();
    static JComboBox<Location> locationBox;
    static MainCanvas canvas;

    MainFrame(String title, User user) {
        super(title);
        MainFrame.user = user;

        new Thread( () -> {
            Object[] list = Request.execute(Request.SORT, Human.class, "id", "ASC");
            humanList = Arrays.stream(list).map( elem -> (Human) elem ).collect(Collectors.toList());
            list = Request.execute(Request.SORT, Location.class, "id", "ASC");
            locationList = Arrays.stream(list).map( elem -> (Location) elem ).collect(Collectors.toList());
        } ).start();

        canvas = new MainCanvas();
        MainTable table = new MainTable();
        locationBox = CustomComponentFactory.getComboBox( new Location[]{} );
        locationBox.addActionListener(e -> MainFrame.canvas.repaint());
        fillLocationBox();

        this.setBounds(0, 0, 1280, 720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setBackground(Color.WHITE);
        container.setForeground(Color.BLACK);
        container.setLayout(new BorderLayout());

        JPanel header = CustomComponentFactory.getEmptyPanel();
        header.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        header.setLayout(new GridLayout(1, 2));
        header.add(CustomComponentFactory.getLabel("Персонажи", SwingConstants.CENTER, 36.0f, false));
        header.add(CustomComponentFactory.getLabel("Профиль", SwingConstants.CENTER, 36.0f, false));

        JPanel right = CustomComponentFactory.getEmptyPanel();
        right.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new MainProfile());
        right.add(Box.createVerticalStrut(20));
        right.add(locationBox);
        right.add(Box.createVerticalStrut(20));
        right.add(canvas);

        container.add(header, BorderLayout.NORTH);
        container.add(table, BorderLayout.WEST);
        container.add(right, BorderLayout.EAST);
    }

    private void fillLocationBox() {
        while (locationList.size() == 0) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        locationBox.removeAllItems();
        locationList.forEach(elem -> locationBox.addItem(elem));
    }
}
