package com.bobisonfire.foodshell.client.guiframe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;

class CustomComponentFactory {
    static JPanel getEmptyPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setForeground(Color.BLACK);
        return panel;
    }

    static JLabel getLabel(String text, int alignment, float fontSize, boolean border) {
        String css;
        switch (alignment) {
            case SwingConstants.CENTER:
                css = "center";
                break;
            case SwingConstants.RIGHT:
                css = "right";
                break;
            default:
                css = "left";
        }

        JLabel label = new JLabel(text, alignment) {
            private String text;
            @Override
            public void setText(String text) {
                this.text = text;
                String htmlText = "<html><div style='text-align: " + css + ";'>" +
                        text.replaceAll("\n", "<br>") +
                        "</div></html>";
                super.setText(htmlText);
            }

            @Override
            public String getText() {
                return text;
            }
        };
        label.setFont(label.getFont().deriveFont(fontSize));
        if (border) label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return label;
    }

    static JTextField getTextField(float fontSize) {
        JTextField textField = new JTextField();
        textField.setMargin(new Insets(0, 10, 0, 10));
        textField.setFont(textField.getFont().deriveFont(fontSize));
        return textField;
    }

    static JTextField getTextField(float fontSize, String placeholder) {
        JTextField textField = getTextField(fontSize);
        textField.addFocusListener(new PlaceholderListener(textField, placeholder));
        return textField;
    }

    static JPasswordField getPasswordField(float fontSize, String placeholder) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMargin(new Insets(0, 10, 0, 10));
        passwordField.setFont(passwordField.getFont().deriveFont(fontSize));
        passwordField.addFocusListener(new PlaceholderListener(passwordField, placeholder));
        return passwordField;
    }

    static <T> JComboBox<T> getComboBox(T[] args) {
        JComboBox<T> comboBox = new JComboBox<>(args);
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(Color.BLACK);
        return comboBox;
    }

    static void showMessage(String message) {
        JFrame messageFrame = new JFrame("Сообщение");
        messageFrame.setBounds(400, 400, 300, 200);
        Container container = messageFrame.getContentPane();
        container.setLayout(new GridBagLayout());

        JLabel messageLabel = getLabel("", SwingUtilities.CENTER, 16.0f, false);
        messageLabel.setText(message);
        JButton closeButton = new JButton();
        closeButton.setAction(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageFrame.setVisible(false);
                messageFrame.dispose();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 1.0;
        c.weighty = 3.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        container.add(messageLabel, c);

        c.weighty = 1.0;
        container.add(closeButton, c);

        messageFrame.setVisible(true);
    }

    static void showChoice(String message, Supplier<?> onOKClick) {
        JFrame choiceFrame = new JFrame("Выбор");
        choiceFrame.setBounds(400, 400, 300, 200);
        Container container = choiceFrame.getContentPane();
        container.setLayout(new GridBagLayout());

        JLabel messageLabel = getLabel("", SwingUtilities.CENTER, 16.0f, false);
        messageLabel.setText(message);
        JButton cancelButton = new JButton();
        JButton okButton = new JButton();
        cancelButton.setAction(new AbstractAction("Отмена") {
            @Override
            public void actionPerformed(ActionEvent e) {
                choiceFrame.setVisible(false);
                choiceFrame.dispose();
            }
        });
        okButton.setAction(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                choiceFrame.setVisible(false);
                choiceFrame.dispose();
                onOKClick.get();
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 2.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;

        container.add(messageLabel, c);

        c.gridwidth = 1;
        c.weightx = 1.0;
        container.add(cancelButton, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        container.add(okButton, c);

        choiceFrame.setVisible(true);

    }
}
