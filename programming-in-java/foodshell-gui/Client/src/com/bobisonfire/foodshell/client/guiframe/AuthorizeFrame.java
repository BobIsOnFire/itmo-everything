package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.Main;
import com.bobisonfire.foodshell.client.entities.User;
import com.bobisonfire.foodshell.client.exchange.Password;
import com.bobisonfire.foodshell.client.exchange.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AuthorizeFrame extends JFrame {
    // VyvGeSaFuk9HinZaMorPi PoCo6GefBo4GyPaz1Kub
    private JTextField emailField = CustomComponentFactory.getTextField(20.0f, "Email");
    private JTextField nameField = CustomComponentFactory.getTextField(20.0f, "Имя");
    private JPasswordField passwordField = CustomComponentFactory.getPasswordField(20.0f, "Пароль");
    private JLabel authLabel = CustomComponentFactory.getLabel("", SwingConstants.CENTER, 20.0f, false);
    private JButton authorizeButton = new JButton();
    private JButton registerButton = new JButton();

    public AuthorizeFrame(String header) {
        super(header);

        this.setBounds(0, 0, 500, 300);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        authorizeButton.setAction(new AuthorizeAction("Вход"));
        registerButton.setAction(new RegisterAction("Регистрация"));

        authorizeLayout();
    }

    private void authorizeLayout() {
        JPanel panel = CustomComponentFactory.getEmptyPanel();
        panel.setLayout(new GridBagLayout());

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        authLabel.setText("Авторизация");

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 2.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);

        panel.add(authLabel, c);
        panel.add(emailField, c);
        panel.add(passwordField, c);

        c.gridwidth = 1;
        c.weightx = 1.0;

        panel.add(authorizeButton, c);
        panel.add(registerButton, c);
    }

    private void registerLayout() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setForeground(Color.BLACK);
        panel.setLayout(new GridBagLayout());

        SwingUtilities.invokeLater( () -> {
            this.getContentPane().removeAll();
            this.getContentPane().add(panel);
            authLabel.setText("Регистрация");
        });

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.insets = new Insets(10, 10, 10, 10);

        panel.add(authLabel, c);
        panel.add(emailField, c);
        panel.add(nameField, c);
        panel.add(registerButton, c);
    }

    private boolean emailInvalid(String email) {
        return !email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)$");
    }

    private class AuthorizeAction extends AbstractAction {
        AuthorizeAction(String name) {
            super(name);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String pword = new String( passwordField.getPassword() );

            if (email.isEmpty() || pword.isEmpty()) {
                authLabel.setText("Введите email и пароль."); // this shit is still not working lol
                return;
            }

            if (emailInvalid(email)) {
                authLabel.setText("Email не соответствует формату.");
                return;
            }

            Object[] list = Request.execute(Request.GET, User.class, "email", email);
            User user = (User) list[0];

            Password password = new Password( pword );
            if (password.getHashCode().equals(user.getPassword())) {
                JFrame frame = new MainFrame("FoodShell v." + Main.FS_VERSION, user);
                frame.setVisible(true);

                AuthorizeFrame.this.setVisible(false);
                AuthorizeFrame.this.dispose();
            } else {
                list = Request.execute(Request.PWGENERATE, user);
                user = (User) list[0];
                authLabel.setText("Пароль неверен. Вам на почту выслан новый.");
                Request.execute(Request.SET, User.class, user);
            }
        }
    }

    private class RegisterAction extends AbstractAction {
        private boolean registerMode = false;

        RegisterAction(String name) {
            super(name);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!registerMode) {
                registerLayout();
                registerMode = true;
                return;
            }

            String email = emailField.getText();
            String name = nameField.getText();

            if (email.isEmpty() || name.isEmpty()) {
                authLabel.setText("Введите email и имя.");
                return;
            }

            if (emailInvalid(email)) {
                authLabel.setText("Email не соответствует формату.");
                return;
            }

            Object[] users = Request.execute(Request.GET, User.class, "email", email);
            if (users.length == 0) {
                User user = new User();
                user.setEmail(email);
                user.setName(name);
                user.setColor(0);

                Object[] list = Request.execute(Request.PWGENERATE, user);
                user = (User) list[0];
                Request.execute(Request.SET, User.class, user);
            }

            authorizeLayout();
            registerMode = false;

            if (users.length == 0) authLabel.setText("Регистрация завершена. Пароль выслан вам на почту.");
            else authLabel.setText("Такой аккаунт уже существует.");
        }
    }
    // todo go through IDEA code inspection when everything is done!
}
