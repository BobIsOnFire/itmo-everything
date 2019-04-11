package com.bobisonfire.foodshell;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerHelper {
    private ServerSocket server;
    private List<Connection> connections = Collections.synchronizedList( new ArrayList<>() );

    public ServerHelper(int port) {
        try {
            server = new ServerSocket(port);
            System.out.println("Сервер доступен по адресу " + server.toString());

            while (true) {
                Socket socket = server.accept();

                Connection con = new Connection(socket);
                connections.add(con);

                con.start();
            }
        } catch(IOException e) {
            System.err.println("Не могу создать сервер");
        } finally {
            closeAll();
        }
    }

    private void closeAll() {
        try {
            server.close();

            for (Connection con: connections) {
                con.close();
            }
        } catch (Exception e) {
            System.err.println("Потоки не были закрыты!");
        }
    }

    public void removeConnection(Connection con) {
        connections.remove(con);
        if (connections.size() == 0) {
            closeAll();
        }
    }
}
