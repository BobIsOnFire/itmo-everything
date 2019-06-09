package com.bobisonfire.foodshell.client.guiframe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class LabelEditListener implements MouseListener {
    // todo validation check
    // todo right mouse click
    @Override
    public void mouseClicked(MouseEvent e) {
        JLabel label = (JLabel) e.getComponent();
        if (!SwingUtilities.isLeftMouseButton(e))
            return;

        if (e.getClickCount() != 2) return;

        JFrame editFrame = new JFrame("Изменение");
        editFrame.setBounds(400, 400, 300, 200);
        Container container = editFrame.getContentPane();
        container.setLayout(new GridBagLayout());

        JLabel editLabel = CustomComponentFactory.getLabel("Введите значение:", SwingUtilities.CENTER, 20.0f, false);
        JTextField textField = CustomComponentFactory.getTextField(20.0f);
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
                    editLabel.setText("Новое значение не может быть пустым.");
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
        container.add(textField, c);

        c.gridwidth = 1;
        c.weightx = 1.0;
        container.add(cancelButton, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        container.add(okButton, c);

        editFrame.setVisible(true);
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
}
