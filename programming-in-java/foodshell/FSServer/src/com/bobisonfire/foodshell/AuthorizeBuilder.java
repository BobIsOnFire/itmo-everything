package com.bobisonfire.foodshell;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.util.function.Function;

/**
 * Класс-строитель, создающий и обрабатывающий процессы регистрации, авторизации и восстановления пароля
 * для интерактивного клиент-серверного консольного приложения.
 */
class AuthorizeBuilder {
    /**
     * Метод, создающий instance регистрации: получение почтового адреса, генерация пароля, отсылка на почту
     * пароля и сохранение пользователя в БД. В случае, если такой адрес уже зарегистрирован, пользователь
     * переносится на восстановление пароля. После успешной регистрации пользователь переносится на авторизацию.
     */
    static Function<SocketChannel, Integer> registerInstance() {
        return socket -> {
            try (DBExchanger exchanger = new DBExchanger()) {
                ServerHelper.writeToChannel(socket, "**Регистрация**\nEmail:");
                String email = readEmail(socket);

                ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE email LIKE ?;", email);
                if (set.next()) {
                    ServerHelper.writeToChannel(socket, "Данный пользователь уже существует. Забыли пароль? y/n");
                    if (ServerHelper.readFromChannel(socket).equals("y")) {
                        forgotPasswordInstance().apply(socket);
                        return authorizeInstance().apply(socket);
                    }
                    ServerHelper.writeToChannel(socket, "Перезапустите FoodShell для повторной авторизации.");
                    return null;
                } else {
                    Password password = new Password();

                    MailSender sender = new MailSender();
                    sender.sendMessage(email, "Ваш пароль к FoodShell", "Привет, ваш пароль:\n" + password.get());

                    ServerHelper.writeToChannel(socket, "Введите имя:");
                    String name = ServerHelper.readFromChannel(socket);

                    exchanger.update("INSERT INTO users(email,password,name) VALUES (?,?,?);",
                            email, password.getHashCode(), name);
                    ServerHelper.writeToChannel(socket, "Регистрация завершена, пароль выслан вам на почту.");
                    return authorizeInstance().apply(socket);
                }
            } catch (NullPointerException exc) {
                return null;
            } catch (Exception exc) {
                System.out.println("Невозможно зарегистрировать пользователя.");
                exc.printStackTrace();
                return null;
            }
        };
    }

    /**
     * Метод, создающий instance авторизации: получение логина и пароля, сопоставление с существующими и
     * возвращение id данного пользователя. Если логина не существует, соединение с пользователем завершается.
     * Если пароль неверный, пользователь перенаправляется на восстановление пароля.
     */
    static Function<SocketChannel, Integer> authorizeInstance() {
        return socket -> {
            try (DBExchanger exchanger = new DBExchanger()) {
                ServerHelper.writeToChannel(socket, "**Авторизация**\nEmail:");
                String email = readEmail(socket);

                ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE email LIKE ?;", email);
                if (!set.next()) {
                    ServerHelper.writeToChannel(socket, "Такого пользователя не существует.");
                    return null;
                }

                ServerHelper.writeToChannel(socket, "Пароль:");
                Password password = new Password(ServerHelper.readFromChannel(socket));

                String correctHash = set.getString("password");
                while ( !password.getHashCode().equals( correctHash ) ) {
                    ServerHelper.writeToChannel(socket, "Пароль некорректен. Забыли пароль? y/n");
                    if (ServerHelper.readFromChannel(socket).equals("y"))
                        correctHash = forgotPasswordInstance().apply(socket);

                    ServerHelper.writeToChannel(socket, "Пароль:");
                    password = new Password(ServerHelper.readFromChannel(socket));
                }

                ServerHelper.writeToChannel(socket, "Авторизация завершена.");
                exchanger.update("UPDATE users SET last_authorize=current_timestamp WHERE id=?;", set.getInt("id"));
                return set.getInt("id");
            } catch (NullPointerException exc) {
                return null;
            }  catch (Exception exc) {
                System.out.println("Невозможно авторизовать пользователя.");
                exc.printStackTrace();
                return null;
            }
        };
    }

    /**
     * Метод, создающий instance восстановления пароля: получение адреса, генерация нового пароля и отправка
     * пароля по данному почтовому адресу. Если адреса не существует, соединение с пользователем завершается.
     */
    private static Function<SocketChannel, String> forgotPasswordInstance() {
        return socket -> {
            try (DBExchanger exchanger = new DBExchanger()) {
                ServerHelper.writeToChannel(socket, "**Восстановление пароля**\nEmail:");
                String email = readEmail(socket);

                Password password = new Password();
                ResultSet set = exchanger.getQuery("SELECT * FROM users WHERE email LIKE ?;", email);
                if (!set.next()) {
                    ServerHelper.writeToChannel(socket, "Пользователя не существует.");
                    return null;
                }

                exchanger.update("UPDATE users SET password=? WHERE email LIKE ?;", password.getHashCode(), email);

                MailSender sender = new MailSender();
                sender.sendMessage(email, "Восстановление пароля к FoodShell",
                       "Привет, вы запросили смену пароля.\n" +
                       "Ваш новый пароль:\n" + password.get());

                ServerHelper.writeToChannel(socket, "Доступ восстановлен, новый пароль выслан вам на почту.");
                return password.getHashCode();
            } catch (NullPointerException exc) {
                return null;
            }  catch (Exception exc) {
                System.out.println("Невозможно восстановить пароль.");
                exc.printStackTrace();
                return null;
            }
        };
    }

    /**
     * Метод, читающий email из данного канала до тех пор, пока он не станет валидным.
     * @param socket текущий канал
     * @return прочитанный email
     */
    private static String readEmail(SocketChannel socket) throws IOException {
        String email = ServerHelper.readFromChannel(socket);
        while (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)$")) {
            ServerHelper.writeToChannel(socket, "Неверный формат email. Попробуйте снова:");
            email = ServerHelper.readFromChannel(socket);
        }
        return email;
    }
}
