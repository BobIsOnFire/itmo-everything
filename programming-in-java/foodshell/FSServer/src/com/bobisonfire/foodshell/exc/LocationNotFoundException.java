package com.bobisonfire.foodshell.exc;

public class LocationNotFoundException extends NotFoundException {

    public LocationNotFoundException(String name) {
        super(name);
    }

    public String getMessage() {
        return "Локация не найдена: " + name;
    }
}
