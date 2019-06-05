package com.bobisonfire.foodshell;

public class ServerException extends Exception {
    public ServerException(String message) {
        super(message);
    }

    public ServerException(String message, Exception cause) {
        super(message, cause);
    }
}
