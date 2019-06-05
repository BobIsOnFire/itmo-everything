package com.bobisonfire.foodshell.exchange;

import com.bobisonfire.foodshell.ServerException;
import com.bobisonfire.foodshell.entities.Human;
import com.bobisonfire.foodshell.entities.Location;
import com.bobisonfire.foodshell.entities.User;
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
            String[] tokens = message.split("\\s+");
            String field = tokens[0];
            String order = tokens[1];

            try (DBExchanger exchanger = new DBExchanger()) {
                ResultSet set = exchanger.getQuery("SELECT * FROM humans ORDER BY ? ?", field, order);
                while (set.next()) {
                    Gson gson = new Gson();
                    String serialized = gson.toJson(Human.from(set), Human.class);
                    socketChannel.write( ByteBuffer.wrap(serialized.getBytes()) );
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
            String[] tokens = message.split("\\s+");
            String field = tokens[0];
            String value = tokens[1];

            try (DBExchanger exchanger = new DBExchanger()) {
                ResultSet set = exchanger.getQuery("SELECT * FROM humans WHERE ? LIKE ?", field, value);
                while (set.next()) {
                    Gson gson = new Gson();
                    String serialized = gson.toJson(Human.from(set), Human.class);
                    socketChannel.write( ByteBuffer.wrap(serialized.getBytes()) );
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
            String[] tokens = message.split("\\s+");
            String className = tokens[0];
            String field = tokens[1];
            String value = tokens[2];

            try (DBExchanger exchanger = new DBExchanger()) {
                String tableName = className + "s";

                ResultSet set = exchanger.getQuery("SELECT * FROM ? WHERE ? LIKE ?", tableName, field, value);
                set.next();

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

                socketChannel.write( ByteBuffer.wrap(serialized.getBytes()) );
            } catch (SQLException exc) {
                throw new ServerException("Ошибка при чтении объекта из БД.", exc);
            } catch (IOException exc) {
                throw new ServerException("Ошибка записи в канал.", exc);
            }
        }
    },

    SET {
        @Override
        protected void run(String message, SocketChannel socketChannel) {

        }
    },

    REMOVE {
        @Override
        protected void run(String message, SocketChannel socketChannel) {

        }
    },

    PWGENERATE {
        @Override
        public void run(String message, SocketChannel socketChannel) {

        }
    };

    protected abstract void run(String message, SocketChannel socketChannel) throws ServerException;

    public static void execute(String req, SocketChannel socketChannel) {
        String[] tokens = req.split("\\s+", 2);
        Request request = Request.valueOf( tokens[0].toUpperCase() );
        try {
            request.run(tokens[1], socketChannel);
        } catch (Exception e) {
            // todo operation logging!!
        }
    }
}
