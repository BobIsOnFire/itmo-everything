package com.bobisonfire.foodshell.client.guiframe;

import javax.swing.*;
import java.awt.*;

class MainProfile extends JPanel {

    private class CustomLabel extends JLabel {
        CustomLabel(String label, int alignment) {
            super(label, alignment);
            this.setFont(this.getFont().deriveFont(24.0f));
        }

        CustomLabel(String label) {
            super(label);
            this.setFont(this.getFont().deriveFont(24.0f));
        }
    }

    MainProfile() {
        super();
        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        this.add(new CustomLabel("Имя", SwingConstants.LEFT), c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(new CustomLabel("%NAME%", SwingConstants.RIGHT), c);

        c.gridwidth = 1;
        this.add(new CustomLabel("Цвет", SwingConstants.LEFT), c);

        c.weightx = 0.0;
        this.add(new CustomLabel("sq"), c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(new CustomLabel("%c%", SwingConstants.RIGHT), c);

        c.gridwidth = 1;
        this.add(new JButton("Сменить пароль"), c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(new JButton("Выйти"), c);
    }
}
