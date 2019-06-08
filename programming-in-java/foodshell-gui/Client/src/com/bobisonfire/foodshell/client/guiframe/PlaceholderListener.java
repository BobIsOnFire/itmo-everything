package com.bobisonfire.foodshell.client.guiframe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class PlaceholderListener implements FocusListener {
    private JTextField textField;
    private String placeholder;
    private boolean empty;

    PlaceholderListener(JTextField textField, String placeholder) {
        this.textField = textField;
        this.placeholder = placeholder;
        focusLost(null);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (empty) {
            textField.setText("");
            textField.setForeground(Color.BLACK);
            empty = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (textField.getText().isEmpty()) {
            textField.setText(placeholder);
            textField.setForeground(Color.GRAY);
            empty = true;
        }
    }
}
