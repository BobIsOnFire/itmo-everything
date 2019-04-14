package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.commands.CommandDoc;
import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.exc.NotFoundException;
import com.bobisonfire.foodshell.exc.TransformerException;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ServerHelper {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer = ByteBuffer.allocate(256);

    public ServerHelper() {

        String ip;
        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 0);
            ip = socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            System.out.println("Not opening the server.");
            return;
        }

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(0));
            serverChannel.configureBlocking(false);

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Сервер доступен по адресу " + ip + ":" +
                    serverChannel.socket().getLocalPort() + "\n");

            Iterator<SelectionKey> iter;
            SelectionKey key;
            while (serverChannel.isOpen()) {
                selector.select();
                iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) handleAccept(key);
                    if (key.isReadable()) handleRead(key);
                }
            }
        } catch(Exception e) {
            System.err.println("Что-то не так с сервером. Закрываюсь...");
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel socket = ( (ServerSocketChannel) key.channel() ).accept();
        socket.configureBlocking(false);

        String[] meta = readFromChannel(socket).split("\\s+");
        String path = meta[0];
        String name = meta[1];
        String isPathOnServer = "false";

        if (path == null || path.equals("")) {
            path = Human.PATH;
            isPathOnServer = "true";
        }

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("path", path);
        map.put("isPathOnServer", isPathOnServer);
        map.put("logUser", "God");

        socket.register(selector, SelectionKey.OP_READ, map);

        System.out.println(name + " вошел. Использует коллекцию по адресу " + path);
        writeToChannel(socket, initializeMessageClient());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socket = (SocketChannel) key.channel();
        String message = readFromChannel(socket);

        Map<String, String> map = (Map<String, String>) key.attachment();

        if (message == null) {
            System.out.println(map.get("name") + " вышел."); // todo: последние проверки?
            socket.close();
            return;
        }

        String[] tokens = readCommand(message);
        if (tokens.length == 0)
            return;

        try {
            Command command = Command.getCommandByName(tokens[0]);
            CommandDoc launcher = new CommandDoc(key);

            command.launch(launcher, Arrays.copyOfRange(tokens, 1, tokens.length));
        } catch (TransformerException exc) {
            writeToChannel(socket, "Неверный формат заданного объекта.");
        } catch (NumberFormatException exc) { // caught if non-numeric words are given as numeric arguments
           writeToChannel(socket, "Часть аргументов не являются числами нужного формата.");
        } catch (NotFoundException exc) {
            writeToChannel(socket, exc.getMessage());
        } catch (IndexOutOfBoundsException exc) { // caught if number of args in console command is insufficient
            writeToChannel(socket,"Неверный вызов команды.");
        }
    }

    private String[] readCommand(String command) {

        String[] tokens = command.split("\\s+");

        int quoteCounter = 0;
        int bracketCounter = 0;
        boolean tokenComplete = true;
        ArrayList<String> result = new ArrayList<>();

        for (String token: tokens) {
            if (token.equals(""))
                continue;

            quoteCounter += token.chars().filter(ch -> ch == '"').count();

            bracketCounter += token.chars().filter(ch -> ch == '{').count()
                    - token.chars().filter(ch -> ch == '}').count();

            String temp = token.replaceAll("\"", "");
            if (tokenComplete)
                result.add(temp);
            else {
                int index = result.size() - 1;
                result.set(index, result.get(index) + " " + temp);
            }

            tokenComplete = quoteCounter % 2 == 0 && bracketCounter == 0;
        }

        return result.toArray(new String[0]);
    }

    private String initializeMessageClient() {
        return "FoodShell v" + ServerMain.VERSION + ". Some rights reserved." +
               "Введите help для списка всех команд.\n";
    }

    private String readFromChannel(SocketChannel socket) {
        StringBuilder sb = new StringBuilder();

        buffer.clear();
        int read;
        try {
            read = socket.read(buffer);
            buffer.flip();
            byte[] bytes = new byte[buffer.limit()];
            buffer.get(bytes);
            sb.append(new String(bytes).trim());
            buffer.clear();
        } catch (IOException exc) {
            read = -1;
        }

        if (read >= 0)
            return sb.toString();

        return null;
    }

    public void writeToChannel(SocketChannel socket, String message) {
        buffer.clear();
        buffer.put(message.getBytes());

        try {
            while (buffer.hasRemaining())
                socket.write(buffer);
        } catch (IOException e) {
            System.out.println("Не могу отправить по каналу.");
        }

        buffer.clear();
    }
}
