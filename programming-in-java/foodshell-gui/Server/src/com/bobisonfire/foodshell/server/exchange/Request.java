package com.bobisonfire.foodshell.server.exchange;

import com.bobisonfire.foodshell.server.ServerException;
import com.bobisonfire.foodshell.server.entities.Coordinate;
import com.bobisonfire.foodshell.server.entities.Human;
import com.bobisonfire.foodshell.server.entities.Location;
import com.bobisonfire.foodshell.server.entities.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.sql.ResultSet;
import java.sql.SQLException;

public enum Request {
    SORT {
        @Override
        protected void run(String message, SocketChannel socketChannel) throws ServerException {
            String[] tokens = message.split("\\s+", 2);
            String field = tokens[0];
            String order = tokens[1];

            try (DBExchanger exchanger = new DBExchanger()) {
                ResultSet set = exchanger.getQuery("SELECT * FROM humans ORDER BY " + field + " " + order);
                while (set.next()) {
                    Gson gson = new Gson();
                    String serialized = gson.toJson(Human.from(set), Human.class);
                    write(socketChannel, serialized);
                }
            } catch (SQLException exc) {
                throw new ServerException("Ошибка при чтении объекта из БД.", exc);
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    },

    FILTER {
        @Override
        protected void run(String message, SocketChannel socketChannel) throws ServerException {
            String[] tokens = message.split("\\s+", 2);
            String field = tokens[0];
            String value = tokens[1];

            try (DBExchanger exchanger = new DBExchanger()) {
                ResultSet set = exchanger.getQuery("SELECT * FROM humans WHERE " + field + " = ?", value);
                while (set.next()) {
                    Gson gson = new Gson();
                    String serialized = gson.toJson(Human.from(set), Human.class);
                    write(socketChannel, serialized);
                }
            } catch (SQLException exc) {
                throw new ServerException("Ошибка при чтении объекта из БД.", exc);
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    },

    GET {
        @Override
        protected void run(String message, SocketChannel socketChannel) throws ServerException {
            String[] tokens = message.trim().split("\\s+", 3);
            String className = tokens[0];
            String field = tokens[1];
            String value = tokens[2];

            try (DBExchanger exchanger = new DBExchanger()) {
                String tableName = className + "s";

                ResultSet set = exchanger.getQuery("SELECT * FROM " + tableName +
                        " WHERE " + field + " LIKE ?", value);
                set.next();
                System.out.println(set.getString("password"));

                String serialized = "";
                Gson gson = new Gson();
                switch (className) {
                    case "User":
                        serialized = gson.toJson(User.from(set), User.class);
                        break;
                    case "Human":
                        serialized = gson.toJson(Human.from(set), Human.class);
                        break;
                    case "Location":
                        serialized = gson.toJson(Location.from(set), Location.class);
                        break;
                }

                write(socketChannel, serialized);
            } catch (SQLException exc) {
                throw new ServerException("Ошибка при чтении объекта из БД.", exc);
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    },

    SET {
        @Override
        protected void run(String message, SocketChannel socketChannel) throws ServerException {
            String[] tokens = message.split("\\s+", 2);
            String className = tokens[0];
            String serialized = tokens[1];

            try (DBExchanger exchanger = new DBExchanger()) {
                int changed = 0;
                Gson gson = new Gson();
                Coordinate crd;

                switch (className) {
                    case "User":
                        User user = gson.fromJson(serialized, User.class);
                        changed = exchanger.update(
                                "INSERT INTO users (email, password, name, color) " +
                                "VALUES (?, ?, ?, ?) ON CONFLICT ON CONSTRAINT users_email_key DO UPDATE SET " +
                                "password = EXCLUDED.password, name = EXCLUDED.name, color = EXCLUDED.color",
                                user.getEmail(), user.getPassword(),
                                user.getName(), user.getColor() // todo add conflict info
                        );
                        break;
                    case "Human":
                        Human human = gson.fromJson(serialized, Human.class);
                        crd = human.getCoordinate();
                        changed = exchanger.update(
                                "INSERT INTO humans (id, creator_id, name, birthday, gender, " +
                                "location_id, creation_date, x, y, z) VALUES(?,?,?,?,?,?,?,?,?,?)",
                                human.getId(), human.getCreatorID(), human.getName(), human.getBirthday(),
                                human.getGender().ordinal(), human.getLocationID(), human.getCreationDate(),
                                crd.getX(), crd.getY(), crd.getZ()
                        );
                        break;
                    case "Location":
                        Location location = gson.fromJson(serialized, Location.class);
                        crd = location.getCoordinate();
                        changed = exchanger.update(
                                "INSERT INTO locations (id, name, size, x, y, z)" +
                                "VALUES (?, ?, ?, ?, ?, ?)",
                                location.getId(), location.getName(), location.getSize(),
                                crd.getX(), crd.getY(), crd.getZ()
                        );
                        break;
                }

                write(socketChannel, String.valueOf(changed));
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    },

    REMOVE {
        @Override
        protected void run(String message, SocketChannel socketChannel) throws ServerException {
            String[] tokens = message.split("\\s+", 2);
            String className = tokens[0];
            int id = Integer.parseInt( tokens[1] );

            try (DBExchanger exchanger = new DBExchanger()) {
                String tableName = className + "s";
                int changed = exchanger.update("DELETE FROM " + tableName + " WHERE id = ?", id);
                write(socketChannel, String.valueOf(changed));
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    },

    PWGENERATE {
        @Override
        protected void run(String message, SocketChannel socketChannel) throws ServerException {
            Gson gson = new Gson();
            Password password = new Password();

            User user = gson.fromJson(message, User.class);
            user.setPassword( password.getHashCode() );

            new Thread( () -> {
                try {
                    MailSender sender = new MailSender();
                    sender.sendMessage(user.getEmail(), "Пароль для FoodShell",
                            "Привет! Ваш пароль от FoodShell:\n" + password.get());
                } catch (Exception exc) {
                    exc.printStackTrace();
                    // todo logging?
                }
            }).start();

            try {
                String serialized = gson.toJson(user, User.class);
                write(socketChannel, serialized);
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    };

    protected abstract void run(String message, SocketChannel socketChannel) throws ServerException;

    public static void execute(String req, SocketChannel socketChannel) {
        System.out.println("Received request: " + req);
        String[] tokens = req.split("\\s+", 2);
        Request request = Request.valueOf( tokens[0].toUpperCase() );
        try {
            request.run(tokens[1], socketChannel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void write(SocketChannel socketChannel, String message) throws IOException {
        String msg = message + "\n";
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
    }
}