package com.bobisonfire.foodshell.guiframe;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private class CustomLabel extends JLabel {
        CustomLabel(String label, int alignment) {
            super(label, alignment);
            this.setFont(this.getFont().deriveFont(36.0f));
        }
    }

    private class CustomComboBox<T> extends JComboBox<T> {
        CustomComboBox(T[] args) {
            super(args);
            this.setBackground(Color.WHITE);
            this.setForeground(Color.BLACK);
        }
    }

    public MainFrame(String header) {
        super(header);
        this.setBounds(0, 0, 1280, 720);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setBackground(Color.WHITE);
        container.setForeground(Color.BLACK);
        container.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(20, 20, 20, 20);
        container.add(new CustomLabel("Персонажи", SwingConstants.CENTER), c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        container.add(new CustomLabel("Профиль", SwingConstants.CENTER), c);
        c.weighty = 5.0;
        c.gridheight = 3;
        c.gridwidth = 1;
        container.add(new MainTable(), c);

        c.gridheight = 1;
        c.weighty = 4.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        container.add(new MainProfile(), c);

        c.weighty = 1.0;
        c.insets = new Insets(0, 20, 20, 20);
        container.add(new CustomComboBox<>( new String[] {"123", "231", "333"} ), c);

        c.weighty = 20.0;
        container.add(new MainCanvas(), c);
    }
}
