package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.Main;
import com.bobisonfire.foodshell.client.entities.User;
import com.bobisonfire.foodshell.client.exchange.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class MainProfile extends JPanel {
    private JPanel colorPanel = new JPanel();
    private JLabel nameLabel = CustomComponentFactory.getLabel("", SwingConstants.RIGHT, 24.0f, false);
    private JLabel colorHexLabel = CustomComponentFactory.getLabel("", SwingConstants.RIGHT, 24.0f, false);
    private JButton newPasswordButton = new JButton();
    private JButton logoutButton = new JButton();

    MainProfile() {
        super();
        String name = MainFrame.user.getName();

        StringBuilder colorHex = new StringBuilder( Integer.toHexString( MainFrame.user.getColor() ) );
        while (colorHex.length() < 6)
            colorHex.insert(0, '0');
        colorHex.insert(0, '#');

        Color color = new Color(MainFrame.user.getColor());

        nameLabel.setText(name);
        nameLabel.addMouseListener(new LabelEditListener());
        nameLabel.addPropertyChangeListener("text", new NameChangeListener());

        colorHexLabel.setText(colorHex.toString());
        colorHexLabel.addMouseListener(new LabelEditListener());
        colorHexLabel.addPropertyChangeListener("text", new ColorChangeListener());

        newPasswordButton.setAction(new NewPasswordAction("Сменить пароль"));
        logoutButton.setAction(new LogoutAction("Выход"));

        colorPanel.setSize(24, 24);
        colorPanel.setBackground(color);

        this.setBackground(Color.WHITE);
        this.setForeground(Color.BLACK);
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.setBounds(660, 100, 600, 250);
        GridBagConstraints c = new GridBagConstraints();

        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);
        c.fill = GridBagConstraints.BOTH;
        this.add(CustomComponentFactory.getLabel("Имя", SwingConstants.LEFT, 24.0f, false), c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(nameLabel, c);

        c.gridwidth = 1;
        this.add(CustomComponentFactory.getLabel("Цвет", SwingConstants.LEFT, 24.0f, false), c);

        c.weightx = 0.1;
        this.add(colorPanel, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(colorHexLabel, c);

        c.gridwidth = 1;
        this.add(newPasswordButton, c);

        c.gridwidth = GridBagConstraints.REMAINDER;
        this.add(logoutButton, c);
    }

    private class LogoutAction extends AbstractAction {
        LogoutAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame self = (JFrame) SwingUtilities.getRoot(MainProfile.this);
            JFrame frame = new AuthorizeFrame("FoodShell v" + Main.FS_VERSION);
            frame.setVisible(true);

            self.setVisible(false);
            self.dispose();
        }
    }

    private class NewPasswordAction extends AbstractAction {
        NewPasswordAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object[] list = Request.execute(Request.PWGENERATE, MainFrame.user);
            MainFrame.user = (User) list[0];
            Request.execute(Request.SET, User.class, MainFrame.user);
            CustomComponentFactory.showMessage("Ваш пароль был изменен.\nНовый пароль выслан вам на почту.");
        }
    }

    private class NameChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            MainFrame.user.setName(nameLabel.getText());
            Request.execute(Request.SET, User.class, MainFrame.user);
        }
    }

    private class ColorChangeListener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            String text = colorHexLabel.getText();
            int hex = Integer.parseInt(text.substring(1), 16);
            MainFrame.user.setColor(hex);
            Request.execute(Request.SET, User.class, MainFrame.user);

            colorPanel.setBackground(new Color(hex));
            MainFrame.canvas.repaint();
        }
    }
}
