package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.Main;
import com.bobisonfire.foodshell.client.entities.Coordinate;
import com.bobisonfire.foodshell.client.entities.Human;
import com.bobisonfire.foodshell.client.entities.Location;
import com.bobisonfire.foodshell.client.entities.User;
import com.bobisonfire.foodshell.client.exchange.Request;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    static MainFrame instance;

    MainFrame(String title, User user) {
        super(title);
        instance = this;
        MainFrame.user = user;

        new Thread( () -> {
            Object[] list = Request.execute(Request.SORT, Human.class, "id", "ASC");
            humanList = Arrays.stream(list).map( elem -> (Human) elem ).collect(Collectors.toList());
            list = Request.execute(Request.SORT, Location.class, "id", "ASC");
            locationList = Arrays.stream(list).map( elem -> (Location) elem ).collect(Collectors.toList());
        } ).start();

        this.setBounds(0, 0, 1280, 800);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        drawMainFrame();
    }

    private void drawMainFrame() {
        JPanel panel = CustomComponentFactory.getEmptyPanel();
        panel.setLayout(new BorderLayout());

        canvas = new MainCanvas();
        MainTable table = new MainTable();
        locationBox = CustomComponentFactory.getComboBox( new Location[]{} );
        locationBox.addActionListener(new LocationBoxListener());
        fillLocationBox();
        JPanel languagePanel = CustomComponentFactory.languageFooter(e -> drawMainFrame());
        languagePanel.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        languagePanel.setSize(0, 50);

        JPanel header = CustomComponentFactory.getEmptyPanel();
        header.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        header.setLayout(new GridLayout(1, 2));
        header.add(CustomComponentFactory.getLabel(Main.R.getString("humans"), SwingConstants.CENTER, 36.0f, false));
        header.add(CustomComponentFactory.getLabel(Main.R.getString("profile"), SwingConstants.CENTER, 36.0f, false));

        JPanel right = CustomComponentFactory.getEmptyPanel();
        right.setBorder(new EmptyBorder(new Insets(20, 20, 20, 20)));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.add(new MainProfile());
        right.add(Box.createVerticalStrut(20));
        right.add(locationBox);
        right.add(Box.createVerticalStrut(20));
        right.add(canvas);

        panel.add(header, BorderLayout.NORTH);
        panel.add(table, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        panel.add(languagePanel, BorderLayout.SOUTH);

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
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

        Location loc = new Location();
        loc.setName("<" + Main.R.getString("create") + ">");
        locationBox.addItem(loc);
    }

    private class LocationBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Location location = (Location) locationBox.getSelectedItem();
            if (location == null) return;

            if (location.getName().equals("<" + Main.R.getString("create") + ">") && location.getCoordinate() == null) {
                JFrame createFrame = new JFrame();
                createFrame.setBounds(300, 300, 300, 300);
                Container container = createFrame.getContentPane();
                container.setBackground(Color.WHITE);
                container.setForeground(Color.BLACK);
                container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

                JLabel infoLabel = CustomComponentFactory.getLabel("", SwingUtilities.CENTER, 16.0f, false);
                infoLabel.setText("<html><div 'text-align: center;'>" + Main.R.getString("location_data_enter") + ":");
                JTextField nameField = CustomComponentFactory.getTextField(16.0f, Main.R.getString("title"));
                JTextField sizeField = CustomComponentFactory.getTextField(16.0f, Main.R.getString("size"));
                JTextField coordinateField = CustomComponentFactory.getTextField(16.0f, Main.R.getString("coordinate_format"));

                JButton cancelButton = new JButton();
                cancelButton.setAction(new AbstractAction( Main.R.getString("cancel") ) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        createFrame.setVisible(false);
                        createFrame.dispose();
                        MainFrame.instance.setEnabled(true);
                    }
                });
                JButton okButton = new JButton();
                okButton.setAction(new AbstractAction("ОК") {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            location.setName(nameField.getText());
                            location.setSize(Integer.parseInt(sizeField.getText()));
                            location.setCoordinate(Coordinate.from(coordinateField.getText()));
                        } catch (NumberFormatException exc) {
                            infoLabel.setText("<html><div 'text-align: center;'>" + Main.R.getString("data_incorrect"));
                            return;
                        }

                        Object[] checklist = Request.execute(Request.GET, Location.class, "name", location.getName());
                        if (checklist.length > 0) {
                            infoLabel.setText("<html><div 'text-align: center;'>" + Main.R.getString("location_exists"));
                            return;
                        }

                        Request.execute(Request.SET, Location.class, location);
                        locationList.clear();
                        new Thread( () -> {
                            Object[] list = Request.execute(Request.SORT, Human.class, "id", "ASC");
                            humanList = Arrays.stream(list).map( elem -> (Human) elem ).collect(Collectors.toList());
                            list = Request.execute(Request.SORT, Location.class, "id", "ASC");
                            locationList = Arrays.stream(list).map( elem -> (Location) elem ).collect(Collectors.toList());
                        } ).start();

                        createFrame.setVisible(false);
                        createFrame.dispose();
                        MainFrame.instance.setEnabled(true);

                        fillLocationBox();
                    }
                });

                JPanel inputPanel = CustomComponentFactory.getEmptyPanel();
                inputPanel.setLayout(new GridLayout(4, 1));
                inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                inputPanel.add(infoLabel);
                inputPanel.add(nameField);
                inputPanel.add(sizeField);
                inputPanel.add(coordinateField);

                JPanel buttonPanel = CustomComponentFactory.getEmptyPanel();
                buttonPanel.setLayout(new GridLayout(1, 2));
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                buttonPanel.add(cancelButton);
                buttonPanel.add(okButton);

                container.add(inputPanel);
                container.add(buttonPanel);

                MainFrame.instance.setEnabled(false);
                createFrame.setVisible(true);
                return;
            }

            MainFrame.canvas.repaint();
        }
    }
}
