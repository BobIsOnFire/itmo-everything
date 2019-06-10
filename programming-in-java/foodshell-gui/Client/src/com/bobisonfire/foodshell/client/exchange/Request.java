package com.bobisonfire.foodshell.client.exchange;

import com.bobisonfire.foodshell.client.entities.User;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public enum Request {
    SORT {
        @Override
        protected Object[] run(Scanner in, PrintWriter out, Object... args) throws IOException {
            Class<?> clazz = (Class<?>) args[0];
            String field = (String) args[1];
            String order = (String) args[2];
            out.printf("SORT %s %s %s\n", clazz.getSimpleName(), field, order);

            List<Object> list = new ArrayList<>();
            Gson gson = new Gson();

            String serialized;
            while( !( serialized = in.nextLine() ).equals("END REQUEST") ) {
                Object object = gson.fromJson(serialized, clazz);
                list.add(object);
            }
            return list.toArray();
        }
    },

    FILTER {
        @Override
        protected Object[] run(Scanner in, PrintWriter out, Object... args) {
            Class<?> clazz = (Class<?>) args[0];
            String field = (String) args[1];
            int value = (int) args[2];
            out.printf("FILTER %s %s %s\n", clazz.getSimpleName(), field, value);

            List<Object> list = new ArrayList<>();
            Gson gson = new Gson();

            String serialized;
            while( !( serialized = in.nextLine() ).equals("END REQUEST") ) {
                Object object = gson.fromJson(serialized, clazz);
                list.add(object);
            }

            return list.toArray();
        }
    },

    GET {
        @Override
        protected Object[] run(Scanner in, PrintWriter out, Object... args) {
            Class<?> clazz = (Class<?>) args[0];
            String field = (String) args[1];
            String value = (String) args[2];

            out.printf("GET %s %s %s\n", clazz.getSimpleName(), field, value);

            Gson gson = new Gson();
            String serialized = in.nextLine();
            return new Object[] {gson.fromJson(serialized, clazz)};
        }
    },

    SET {
        @Override
        protected Object[] run(Scanner in, PrintWriter out, Object... args) {
            Class<?> clazz = (Class<?>) args[0];
            Object instance = args[1];

            Gson gson = new Gson();
            String serialized = gson.toJson(instance, clazz);

            out.printf("SET %s %s\n", clazz.getSimpleName(), serialized);
            int completed = Integer.parseInt( in.nextLine() );
            return new Object[] {completed};
        }
    },

    REMOVE {
        @Override
        protected Object[] run(Scanner in, PrintWriter out, Object... args) {
            Class<?> clazz = (Class<?>) args[0];
            int id = (int) args[1];

            out.printf("REMOVE %s %d\n", clazz.getSimpleName(), id);
            int completed = Integer.parseInt( in.nextLine() );
            return new Object[] {completed};
        }
    },

    PWGENERATE {
        @Override
        public Object[] run(Scanner in, PrintWriter out, Object... args) {
            User user = (User) args[0];

            Gson gson = new Gson();
            String serialized = gson.toJson(user, User.class);

            out.printf("PWGENERATE %s\n", serialized);
            serialized = in.nextLine();
            user = gson.fromJson(serialized, User.class);
            return new Object[] {user};
        }
    };

    public final static String ASC = "ASC";
    public final static String DESC = "DESC";
    private static Socket socket;

    protected abstract Object[] run(Scanner in, PrintWriter out, Object... args) throws IOException;

    public static void setSocket(Socket socket) {
        Request.socket = socket;
    }

    public static Object[] execute(Request req, Object... args) { // todo put execution into a new thread
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            return req.run(in, out, args);
        } catch (IOException exc) {
            exc.printStackTrace();
            return new Object[0];
        }
    }
}
