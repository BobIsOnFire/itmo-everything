package com.bobisonfire.foodshell.client.guiframe;

import com.bobisonfire.foodshell.client.Main;
import com.bobisonfire.foodshell.client.entities.User;
import com.bobisonfire.foodshell.client.exchange.Password;
import com.bobisonfire.foodshell.client.exchange.Request;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AuthorizeFrame extends JFrame {
    // MiSezGym0PyCahFenCiDo4 PoCo6GefBo4GyPaz1Kub
    private JTextField emailField = CustomComponentFactory.getTextField(20.0f, "Email");
    private JTextField nameField = CustomComponentFactory.getTextField(20.0f);
    private JPasswordField passwordField = CustomComponentFactory.getPasswordField(20.0f);
    private JLabel authLabel = CustomComponentFactory.getLabel("", SwingConstants.CENTER, 20.0f, false);
    private JButton authorizeButton = new JButton();
    private JButton registerButton = new JButton();

    public AuthorizeFrame(String header) {
        super(header);

        this.setBounds(0, 0, 500, 400);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        authorizeButton.setAction(new AuthorizeAction());
        registerButton.setAction(new RegisterAction());

        authorizeLayout();
    }

    private void authorizeLayout() {
        JPanel panel = CustomComponentFactory.getEmptyPanel();
        panel.setLayout(new GridBagLayout());
        JPanel languagePanel = CustomComponentFactory.languageFooter(e -> authorizeLayout());
        nameField.addFocusListener( new PlaceholderListener(nameField, Main.R.getString("name")) );
        passwordField.addFocusListener( new PlaceholderListener(passwordField, Main.R.getString("password")) );
        authorizeButton.setText( Main.R.getString("login") );
        registerButton.setText( Main.R.getString("register") );

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

        c.gridwidth = GridBagConstraints.REMAINDER;
        panel.add(registerButton, c);

        c.weightx = 2.0;
        panel.add(languagePanel, c);

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        authLabel.setText( Main.R.getString("authorize") );
    }

    private void registerLayout() {
        JPanel panel = CustomComponentFactory.getEmptyPanel();
        panel.setLayout(new GridBagLayout());
        JPanel languagePanel = CustomComponentFactory.languageFooter(e -> registerLayout());

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
        panel.add(languagePanel, c);

        this.getContentPane().removeAll();
        this.getContentPane().add(panel);
        authLabel.setText( Main.R.getString("register") );
    }

    private boolean emailInvalid(String email) {
        return !email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)$");
    }

    private class AuthorizeAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            String email = emailField.getText();
            String pword = new String( passwordField.getPassword() );

            if (emailInvalid(email)) {
                authLabel.setText( Main.R.getString("email_format_incorrect") );
                return;
            }

            Object[] list = Request.execute(Request.GET, User.class, "email", email);
            if (list.length == 0) {
                authLabel.setText( Main.R.getString("user_not_found") );
                return;
            }
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
                authLabel.setText( Main.R.getString("password_incorrect") );
                Request.execute(Request.SET, User.class, user);
            }
        }
    }

    private class RegisterAction extends AbstractAction {
        private boolean registerMode = false;
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!registerMode) {
                registerLayout();
                registerMode = true;
                return;
            }

            String email = emailField.getText();
            String name = nameField.getText();

            if (emailInvalid(email)) {
                authLabel.setText( Main.R.getString("email_format_incorrect") );
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

            if (users.length == 0) authLabel.setText( Main.R.getString("register_complete") );
            else authLabel.setText( Main.R.getString("user_exists") );
        }
    }
}
