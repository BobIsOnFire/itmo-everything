package com.bobisonfire.foodshell;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

class TCPServerRunner {
    private ServerSocketChannel serverSocketChannel;
    private Selector selector;

    private ServerHandler<SelectionKey> onAccept;
    private ServerHandler<SelectionKey> onRead;

    static TCPServerRunner instance() {
        return new TCPServerRunner();
    }

    void run(int port) throws ServerException {
        if (onAccept == null || onRead == null) {
            throw new ServerException("Обработчики соединения не определены");
        }

        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.configureBlocking(false);

            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.printf("Открываю сервер на %s:%d", InetAddress.getLocalHost(), port);

            while (serverSocketChannel.isOpen()) {
                selector.select();
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();

                    if (key.isAcceptable()) onAccept.accept(key);
                    if (key.isReadable()) onRead.accept(key);
                }
            }
        }
        catch (IOException exc) {
            throw new ServerException("Ошибка подключения к серверу", exc);
        }
    }

    TCPServerRunner setOnAccept(ServerHandler<SelectionKey> onAccept) {
        this.onAccept = onAccept;
        return this;
    }

    TCPServerRunner setOnRead(ServerHandler<SelectionKey> onRead) {
        this.onRead = onRead;
        return this;
    }
}
