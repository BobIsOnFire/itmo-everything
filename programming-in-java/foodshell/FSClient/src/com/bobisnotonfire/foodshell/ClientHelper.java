package com.bobisnotonfire.foodshell;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.CharBuffer;

public class ClientHelper {
    private BufferedReader in;
    private PrintWriter out;
    private Socket socket;
    private String path;
    private String name;

    public ClientHelper(String ip, int port, String name, String path) {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 1000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            this.path = path;
            this.name = name;

            out.println(path + " " + name);
        } catch (IOException exc) {
            System.err.println("Не могу установить соединение. Перезапустите программу.");
            System.exit(0);
        }
    }

    public Thread receiveOutput() {
        Receiver thread = new Receiver(path, name);
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

    public void setSoTimeout(int i) {
        try {
            socket.setSoTimeout(i);
        }
        catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private class Receiver extends Thread {
        private boolean stopped;
        private String path;
        private String name;

        public void setStopped() {
            stopped = true;
        }

        public Receiver(String path, String name) {
            this.path = path;
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

                    System.out.print(str);
                    buffer.clear();
                }

            } catch (IOException e) {
            } finally {
                setStopped();
            }
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
