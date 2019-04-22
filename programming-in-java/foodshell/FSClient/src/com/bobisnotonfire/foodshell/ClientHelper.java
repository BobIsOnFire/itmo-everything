package com.bobisnotonfire.foodshell;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Scanner;

public class ClientHelper {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private String name;

    public ClientHelper(String ip, int port, String name) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            this.name = name;

            out.println(name);
        } catch (IOException exc) {
            System.err.println("Не могу установить соединение. Перезапустите программу.");
            System.exit(0);
        }
    }

    public Thread receiveOutput() {
        Receiver thread = new Receiver(name);
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

    public boolean receiverStopped(Thread receiver) {
        return ( (Receiver) receiver ).stopped;
    }

    public void exportFile(String path) {
        try (Scanner scanner = new Scanner(new FileReader(path))) {
            while (scanner.hasNextLine())
                out.println(scanner.nextLine());
            out.println("!endexport");
        }
        catch (IOException exc) {
            System.out.println("Невозможно прочитать файл " + path);
        }
    }

    private class Receiver extends Thread {
        private boolean stopped;
        private String name;

        public void setStopped() {
            stopped = true;
        }

        public Receiver(String name) {
            this.name = name;
        }

        public void run() {
            try {
                CharBuffer buffer = CharBuffer.allocate(256);
                while (in.read(buffer) > 0) {
                    buffer.flip();
                    char[] chars = new char[buffer.limit()];
                    buffer.get(chars);
                    String str = new String(chars);

                    if (str.startsWith("!import ")) {
                        String p = str.trim().split("\\s+")[1];
                        importFile(p);
                    }
                    else {
                        System.out.print(str);
                    }
                    buffer.clear();
                }

            } catch (IOException e) {
            } finally {
                setStopped();
            }
        }
    }

    private void importFile(String path) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path, false))) {
            CharBuffer buffer = CharBuffer.allocate(256);
            while (in.read(buffer) > 0) {
                buffer.flip();
                char[] chars = new char[buffer.limit()];
                buffer.get(chars);
                String str = new String(chars);

                if (str.contains("!endimport")) {
                    str = str.substring(0, str.indexOf("!endimport"));
                    writer.write(str);
                    break;
                }

                writer.write(str);
                buffer.clear();
            }
        } catch (IOException e) {
            System.out.println("Невозможно записать в файл " + path);
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch(IOException e) {
            System.err.println("Потоки не закрыты.");
        }
    }
}
