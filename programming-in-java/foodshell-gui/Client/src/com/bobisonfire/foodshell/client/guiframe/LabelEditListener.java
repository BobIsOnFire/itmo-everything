package com.bobisonfire.foodshell.client.guiframe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Predicate;

public class LabelEditListener implements MouseListener {
    private Predicate<String> validator;
    private String format;
    LabelEditListener(Predicate<String> validator, String format) {
        this.validator = validator;
        this.format = format;
    }
    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem item = new JMenuItem("Изменить...");
            item.addActionListener(evt -> createEditor(e));
            menu.add(item);
            menu.show(e.getComponent(), e.getX(), e.getY());
            return;
        }

        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
            createEditor(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JLabel label = (JLabel) e.getComponent();
        label.setForeground(Color.BLUE);
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JLabel label = (JLabel) e.getComponent();
        label.setForeground(Color.BLACK);
    }

    private void createEditor(MouseEvent e) {
        JLabel label = (JLabel) e.getComponent();
        JFrame editFrame = new JFrame("Изменение");
        editFrame.setBounds(400, 400, 300, 300);
        Container container = editFrame.getContentPane();
        container.setLayout(new GridBagLayout());

        JLabel editLabel = CustomComponentFactory.getLabel("Введите значение:", SwingUtilities.CENTER, 20.0f, false);
        JLabel formatLabel = CustomComponentFactory.getLabel("", SwingUtilities.CENTER, 16.0f, false);
        formatLabel.setText("<html><div style='text-align: center;'>Формат: " + format);
        JTextField textField = CustomComponentFactory.getTextField(16.0f);
        JButton cancelButton = new JButton();
        JButton okButton = new JButton();

        cancelButton.setAction(new AbstractAction("Отмена") {
            @Override
            public void actionPerformed(ActionEvent e) {
                editFrame.setVisible(false);
                editFrame.dispose();
            }
        });

        okButton.setAction(new AbstractAction("ОК") {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textField.getText();
                if (text.isEmpty()) {
                    editLabel.setText("<html><div style='text-align: center;'>Новое значение не может быть пустым.");
                    return;
                }
                if (!validator.test(text)) {
                    editLabel.setText("<html><div style='text-align: center;'>Значение не соответствует формату.");
                    return;
                }
                editFrame.setVisible(false);
                editFrame.dispose();
                label.setText(text.trim());
            }
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(10, 10, 10, 10);
        c.weightx = 2.0;
        c.weighty = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;

        container.add(editLabel, c);
        container.add(formatLabel, c);
        container.add(textField, c);

        c.gridwidth = 1;
        c.weightx = 1.0;
        container.add(cancelButton, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        container.add(okButton, c);

        editFrame.setVisible(true);
    }
}
