package com.bobisonfire.foodshell.server;

public class ServerRuntimeException extends RuntimeException {
    public ServerRuntimeException(String message) {
        super(message);
    }

    public ServerRuntimeException(String message, Exception cause) {
        super(message, cause);
    }
}
