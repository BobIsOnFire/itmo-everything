package com.bobisonfire.foodshell.guiframe;

import javax.swing.*;
import java.awt.*;

public class AuthorizeFrame extends JFrame {


    public AuthorizeFrame(String header) {
        super(header);
        this.setBounds(0, 0, 300, 200);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setBackground(Color.WHITE);
        container.setForeground(Color.BLACK);
        container.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);

        container.add(new JTextField(), c);
        container.add(new JPasswordField(), c);
        container.add(new JButton("Вход"), c);
    }
}
