package com.bobisonfire.foodshell.entities;

import com.bobisonfire.foodshell.ServerRuntimeException;

public enum Gender {
    MALE, FEMALE, CUTE_CAT, SHREK, GENDERFLUID_HELISEXUAL,
    ANDROGYNY, GENDERLESS, BIGENDER, GENDERQUEER, NOT_SURE_GENDER,
    ROBOT, FAT_DOG, CHARMANDER, CHARMELEON, CHARIZARD;

    public static Gender getGenderByNumber(int i) {
        if (i < 0) return MALE;
        if (i > 14) return CHARIZARD;
        return Gender.values()[i];
    }

    public String getName() {
        String name = this.name();
        name = name.substring(0, 1) + name.substring(1).toLowerCase();
        name = name.replaceAll("_", " ");
        return name;
    }

    public static void printPostulate() {
        System.out.println("\t\t** Существует только 15 гендеров. **");
    }

    public static Gender getGenderByName(String name) {
        try {
            return Gender.valueOf(name.toUpperCase().replaceAll("\\s", "_"));
        }
        catch(IllegalArgumentException exc) {
            throw new ServerRuntimeException("Гендер не найден: " + name);
        }
    }
}
