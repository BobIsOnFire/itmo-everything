package com.bobisonfire.foodshell;

import com.bobisonfire.foodshell.commands.Command;
import com.bobisonfire.foodshell.commands.CommandDoc;
import com.bobisonfire.foodshell.entity.Human;
import com.bobisonfire.foodshell.entity.Location;
import com.bobisonfire.foodshell.exc.NotFoundException;
import com.bobisonfire.foodshell.exc.TransformerException;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Класс, реализующий неблокирующий сервер, принмающий и отдающий информацию, а также
 * организовывающий передачу работы командному ядру <i>FoodShell</i>.
 */
public class ServerHelper {
    private ServerSocketChannel serverChannel;
    private Selector selector;
    private final FileIOHelper f = new FileIOHelper();

    /**
     * Создание сервера и вывод его адреса, обработка критических ошибок.
     */
    public ServerHelper() {

        try {
            serverChannel = ServerSocketChannel.open();
            serverChannel.socket().bind(new InetSocketAddress(0));
            serverChannel.configureBlocking(false);

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Сервер доступен по адресу " + InetAddress.getLocalHost() + ":" +
                    serverChannel.socket().getLocalPort() + "\n");

        } catch(Exception e) {
            System.err.println("Что-то не так с сервером. Закрываюсь...");
            ServerMain.logException(e);
        }
    }

    /**
     * Организация работы сервера: итерация по селектору в поиске новых каналов и чтении существующих.
     */
    public void runServer() {
        try {
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
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обработка нового соединения: сохранение имени пользователя и пути к его коллекции с персонажами,
     * вывод приветственного сообщения и передача в селектор соединения для чтения.
     * @param key ключ соединения
     */
    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel socket = ( (ServerSocketChannel) key.channel() ).accept();

        String[] meta = readFromChannel(socket).split("\\s+");
        String path = meta[0];
        String name = meta[1];
        String isPathOnServer = "false";

        if (path == null || path.equals("")) {
            path = Human.PATH;
            isPathOnServer = "true";
        }

        if (!new File(path).exists())
            f.writeCSVSetIntoFile(Collections.singleton(new Human()), path);

        if (!new File(Location.PATH).exists())
            f.writeCSVSetIntoFile(Collections.singleton(new Location()), Location.PATH);

        Map<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("path", path);
        map.put("isPathOnServer", isPathOnServer);
        map.put("logUser", "God");

        socket.configureBlocking(false);
        socket.register(selector, SelectionKey.OP_READ, map);

        System.out.println(name + " вошел. Использует коллекцию по адресу " + path);
        writeToChannel(socket, initializeMessageClient());
    }

    /**
     * Обработка получаемого сообщения - команды или сигнала выхода. Если пользователь выходит, закрывает
     * его соединение. Если пользователь отправил команду, передает соединение обработчику команд <i>FoodShell</i>.
     * @param key
     * @throws IOException
     */
    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel socket = (SocketChannel) key.channel();
        String message = readFromChannel(socket);

        Map<String, String> map = (Map<String, String>) key.attachment();

        if (message == null) {
            System.out.println(map.get("name") + " вышел.");
            socket.socket().close();
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
           exc.printStackTrace();
        } catch (NotFoundException exc) {
            writeToChannel(socket, exc.getMessage());
        } catch (IndexOutOfBoundsException exc) { // caught if number of args in console command is insufficient
            writeToChannel(socket,"Неверный вызов команды.");
        }
    }

    /**
     * Обработка и токенизация команд из строки.
     */
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
        return "FoodShell v" + ServerMain.VERSION + ". Some rights reserved.\n" +
               "Введите help для списка всех команд.\n";
    }

    /**
     * Организовывает чтение строки из канала.
     */
    private String readFromChannel(SocketChannel socket) {
        StringBuilder sb = new StringBuilder();

        ByteBuffer readBuffer = ByteBuffer.allocate(256);
        int read;
        try {
            read = socket.read(readBuffer);
            readBuffer.flip();
            byte[] bytes = new byte[readBuffer.limit()];
            readBuffer.get(bytes);
            sb.append(new String(bytes));
            readBuffer.clear();
        } catch (IOException exc) {
            read = -1;
        }

        if (read >= 0)
            return sb.toString();

        return null;
    }

    /**
     * Организовывает запись строки в канал.
     */
    public void writeToChannel(SocketChannel socket, String message) {
        String msg = message + "\n";
        ByteBuffer writeBuffer = ByteBuffer.wrap(msg.getBytes());

        try {
            while (writeBuffer.hasRemaining())
                socket.write(writeBuffer);
        } catch (IOException e) {
            System.out.println("Не могу отправить по каналу.");
        }

        writeBuffer.clear();
    }
}
