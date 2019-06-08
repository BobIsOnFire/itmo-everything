package com.bobisonfire.foodshell.server;

public class ServerException extends Exception {
    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Exception cause) {
        super(message, cause);
    }
}
