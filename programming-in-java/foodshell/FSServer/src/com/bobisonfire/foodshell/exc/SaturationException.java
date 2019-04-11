package com.bobisonfire.foodshell.exc;

import com.bobisonfire.foodshell.entity.Human;

public class SaturationException extends RuntimeException {
    private Human causeHuman;

    public SaturationException(Human cause) {
        causeHuman = cause;
    }

    public Human getCauseHuman() {
        return causeHuman;
    }

    @Override
    public String getMessage() {
        return "Невозможно накормить " + causeHuman.getName() + ": защита от перекормления.";
    }
}
