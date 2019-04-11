package com.bobisonfire.foodshell.exc;

public class GenderNotFoundException extends NotFoundException {
    public GenderNotFoundException(String name) {
        super(name);
    }

    public String getMessage() {
        return "Гендер не найден: " + name + " (может, он и существует, но проверьте его наличие здесь командой gender)";
    }
}
