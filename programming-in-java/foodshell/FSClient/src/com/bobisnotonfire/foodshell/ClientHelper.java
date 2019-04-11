package com.bobisnotonfire.foodshell;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientHelper {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;

    // todo закрыть потоки при ошибках или окончании программы
    public ClientHelper(String ip, int port, String name) {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println(name);
        } catch (IOException exc) {
            System.err.println("Не могу установить соединение. Перезапустите программу.");
            System.exit(0);
        }
    }

    public Thread receiveOutput(String path) {
        Receiver thread = new Receiver(path);
        thread.start();
        return thread;
    }

    public void uploadCommand(String command) {
        out.println(command);
    }

    public void stopReceiver(Thread receiver) {
        ( (Receiver) receiver ).setStopped();
        close();
    }

    private class Receiver extends Thread {
        private boolean stopped;
        private String path;

        public void setStopped() {
            stopped = true;
        }

        public Receiver(String path) {
            this.path = path;
        }

        public void run() {
            try {
                if (path == null) {
                    out.println("");
                } else {
                    File file = new File(path);
                    Scanner scanner = new Scanner(new FileInputStream(file));

                    while (scanner.hasNextLine()) {
                        out.println(scanner.nextLine());
                    }
                }

                while (!stopped)
                    System.out.println( in.readLine() );
            } catch (IOException e) {
                System.err.println("Не могу получить сообщение."); // todo логировать ошибочки
            }
        }
    }

    private void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch(IOException e) {
            System.err.println("Потоки не закрыты.");
        }
    }
}
