package com.bobisonfire.foodshell.exc;

public class HumanNotFoundException extends NotFoundException {
    public HumanNotFoundException(String name) {
        super(name);
    }

    public String getMessage() {
        return "Персонаж не найден: " + name;
    }
}
