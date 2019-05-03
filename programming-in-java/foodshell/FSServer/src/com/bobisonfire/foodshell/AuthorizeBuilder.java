package com.bobisonfire.foodshell;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.util.function.Function;

public class AuthorizeBuilder {
    public static Function<SocketChannel, Integer> registerInstance() {
        return socket -> {
            try (DBExchanger exchanger = new DBExchanger()) {
                ServerHelper.writeToChannel(socket, "**Registration**\nEmail:");
                String email = readEmail(socket);

                ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE email LIKE '" + email + "';");
                if (set.next()) {
                    ServerHelper.writeToChannel(socket, "This user already exists. Forgot password? y/n");
                    if (ServerHelper.readFromChannel(socket).equals("y")) {
                        forgotPasswordInstance().apply(socket);
                        return authorizeInstance().apply(socket);
                    }
                    ServerHelper.writeToChannel(socket, "Restart Chat to register/authorize again.");
                    return null;
                } else {
                    Password password = new Password();

                    // commented because something is not working on helios
                     MailSender sender = new MailSender();
                     sender.sendMessage(email, "Your password to Chat", "Hello, here is your password:\n" + password.get());

                    ServerHelper.writeToChannel(socket, "Enter your username:");
                    String name = ServerHelper.readFromChannel(socket);

                    System.out.println(password.getHashCode());
                    exchanger.update("INSERT INTO users(email,password,name) VALUES ('" +
                            email + "','" + password.getHashCode() + "','" + name + "');");
                    ServerHelper.writeToChannel(socket, "Registration complete, password has been sent to your email.");
                    return authorizeInstance().apply(socket);
                }
            } catch (NullPointerException exc) {
                return null;
            } catch (Exception exc) {
                System.out.println("Cannot register user.");
                exc.printStackTrace();
                return null;
            }
        };
    }

    public static Function<SocketChannel, Integer> authorizeInstance() {
        return socket -> {
            try (DBExchanger exchanger = new DBExchanger()) {
                ServerHelper.writeToChannel(socket, "**Authorization**\nEmail:");
                String email = readEmail(socket);

                ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE email LIKE '" + email + "';");
                if (set.next()) {
                    ServerHelper.writeToChannel(socket, "Password:");
                    Password password = new Password(ServerHelper.readFromChannel(socket));

                    String correctHash = set.getString("password");
                    System.out.println(correctHash + "\n" + password.getHashCode());
                    System.out.println(password.getHashCode().equals( correctHash ));
                    while ( !password.getHashCode().equals( correctHash ) ) {
                        ServerHelper.writeToChannel(socket, "Password is incorrect. Forgot password? y/n");
                        if (ServerHelper.readFromChannel(socket).equals("y")) {
                            correctHash = forgotPasswordInstance().apply(socket);
                        } else {
                            ServerHelper.writeToChannel(socket, "Password:");
                            password = new Password(ServerHelper.readFromChannel(socket));
                        }
                    }

                    ServerHelper.writeToChannel(socket, "Authorization complete.");
                    return set.getInt("id");
                } else {
                    ServerHelper.writeToChannel(socket, "This user does not exist.");
                    return null;
                }

            } catch (NullPointerException exc) {
                return null;
            }  catch (Exception exc) {
                System.out.println("Cannot authorize user.");
                exc.printStackTrace();
                return null;
            }
        };
    }

    private static Function<SocketChannel, String> forgotPasswordInstance() {
        return socket -> {
            try (DBExchanger exchanger = new DBExchanger()) {
                ServerHelper.writeToChannel(socket, "**Forgot password**\nEmail:");
                String email = readEmail(socket);

                Password password = new Password();
                ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE email LIKE '" + email + "';");
                set.next();
                String name = set.getString("name");

                System.out.println(set.getString("password") + "\n" + password.getHashCode());

                exchanger.update("UPDATE users SET password='" + password.getHashCode() +
                        "' WHERE email LIKE '" + email + "';");

                MailSender sender = new MailSender();
                sender.sendMessage(email, "Restoring password to Chat",
                       "Hello, you asked to restore the password.\n" +
                       "Here is the new one:\n" + password.get());

                ServerHelper.writeToChannel(socket, "Password is restored.");
                return password.getHashCode();
            } catch (NullPointerException exc) {
                return null;
            }  catch (Exception exc) {
                System.out.println("Cannot restore password.");
                exc.printStackTrace();
                return null;
            }
        };
    }

    private static String readEmail(SocketChannel socket) throws IOException {
        String email = ServerHelper.readFromChannel(socket);
        while (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)$")) {
            ServerHelper.writeToChannel(socket, "Invalid email. Try again:");
            email = ServerHelper.readFromChannel(socket);
        }
        return email;
    }
}
