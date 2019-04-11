package com.bobisonfire.foodshell.exc;

public class CommandNotFoundException extends NotFoundException {
    public CommandNotFoundException ( String cause ) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Команда не найдена: " + name;
    }
}
