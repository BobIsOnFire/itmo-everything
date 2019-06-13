package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Supplier;

class CustomComponentFactory {
    private static JLabel activeLanguage;

    static JPanel getEmptyPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setForeground(Color.BLACK);
        return panel;
    }

    static JLabel getLabel(String text, int alignment, float fontSize, boolean border) {
        JLabel label = new JLabel(text, alignment);
        label.setFont(label.getFont().deriveFont(fontSize).deriveFont(Font.PLAIN));
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

    static JPasswordField getPasswordField(float fontSize) {
        JPasswordField passwordField = new JPasswordField();
        passwordField.setMargin(new Insets(0, 10, 0, 10));
        passwordField.setFont(passwordField.getFont().deriveFont(fontSize));
        return passwordField;
    }

    static JPasswordField getPasswordField(float fontSize, String placeholder) {
        JPasswordField passwordField = getPasswordField(fontSize);
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
        messageLabel.setText("<html><div style='text-align: center;'>" + message);
        JButton closeButton = new JButton();
        closeButton.setAction(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                messageFrame.setVisible(false);
                messageFrame.dispose();
                MainFrame.instance.setEnabled(true);
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

        MainFrame.instance.setEnabled(false);
        messageFrame.setVisible(true);
    }

    static void showChoice(String message, Supplier<?> onOKClick) {
        JFrame choiceFrame = new JFrame("Выбор");
        choiceFrame.setBounds(400, 400, 300, 200);
        Container container = choiceFrame.getContentPane();
        container.setLayout(new GridBagLayout());

        JLabel messageLabel = getLabel("", SwingUtilities.CENTER, 16.0f, false);
        messageLabel.setText("<html><div style='text-align: center;'>" + message);
        JButton cancelButton = new JButton();
        JButton okButton = new JButton();
        cancelButton.setAction(new AbstractAction( Main.R.getString("cancel") ) {
            @Override
            public void actionPerformed(ActionEvent e) {
                choiceFrame.setVisible(false);
                choiceFrame.dispose();
                MainFrame.instance.setEnabled(true);
            }
        });
        okButton.setAction(new AbstractAction("OK") {
            @Override
            public void actionPerformed(ActionEvent e) {
                choiceFrame.setVisible(false);
                choiceFrame.dispose();
                MainFrame.instance.setEnabled(true);
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

        MainFrame.instance.setEnabled(false);
        choiceFrame.setVisible(true);
    }

    static JPanel languageFooter(Consumer<?> afterTask) {
        Locale russian = Locale.forLanguageTag("ru-RU");
        Locale romanian = Locale.forLanguageTag("ro-RO");
        Locale greek = Locale.forLanguageTag("el-GR");
        Locale puerto = Locale.forLanguageTag("es-PR");

        JPanel panel = getEmptyPanel();
        panel.setLayout(new GridLayout(1, 4));
        JLabel russianLabel =
                CustomComponentFactory.getLabel(russian.getDisplayLanguage(Locale.US), SwingUtilities.CENTER, 12.0f, true);
        JLabel romanianLabel =
                CustomComponentFactory.getLabel(romanian.getDisplayLanguage(Locale.US), SwingUtilities.CENTER, 12.0f, true);
        JLabel greekLabel =
                CustomComponentFactory.getLabel(greek.getDisplayLanguage(Locale.US), SwingUtilities.CENTER, 12.0f, true);
        JLabel puertoLabel =
                CustomComponentFactory.getLabel(puerto.getDisplayLanguage(Locale.US), SwingUtilities.CENTER, 12.0f, true);

        switch (Main.R.getLocale().toString()) {
            case "ru_RU":
                activeLanguage = russianLabel;
                break;
            case "ro_RO":
                activeLanguage = romanianLabel;
                break;
            case "el_GR":
                activeLanguage = greekLabel;
                break;
            case "es_PR":
                activeLanguage = puertoLabel;
                break;
            default:
                activeLanguage = new JLabel();
        }

        activeLanguage.setFont(activeLanguage.getFont().deriveFont(Font.BOLD));
        russianLabel.addMouseListener(new LanguageMouseAdapter(russian, afterTask));
        romanianLabel.addMouseListener(new LanguageMouseAdapter(romanian, afterTask));
        greekLabel.addMouseListener(new LanguageMouseAdapter(greek, afterTask));
        puertoLabel.addMouseListener(new LanguageMouseAdapter(puerto, afterTask));

        panel.add(russianLabel);
        panel.add(romanianLabel);
        panel.add(greekLabel);
        panel.add(puertoLabel);

        return panel;
    }

    private static class LanguageMouseAdapter extends MouseAdapter {
        private Locale locale;
        private Consumer<?> afterTask;

        LanguageMouseAdapter(Locale locale, Consumer<?> afterTask) {
            this.locale = locale;
            this.afterTask = afterTask;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            JLabel self = (JLabel) e.getComponent();
            self.setFont(self.getFont().deriveFont(Font.BOLD));
            activeLanguage.setFont(activeLanguage.getFont().deriveFont(Font.PLAIN));
            activeLanguage = self;
            Main.R = ResourceBundle.getBundle("strings", locale);
            afterTask.accept(null);
        }
    }
}
