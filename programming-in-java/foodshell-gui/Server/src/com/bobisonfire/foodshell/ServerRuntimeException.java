package com.bobisonfire.foodshell;

public class ServerRuntimeException extends RuntimeException {
    public ServerRuntimeException(String message) {
        super(message);
    }

    public ServerRuntimeException(String message, Exception cause) {
        super(message, cause);
    }
}
