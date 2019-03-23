package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.exc.GenderNotFoundException;

public enum Gender {
    MALE, FEMALE, CUTE_CAT, SHREK, GENDERFLUID_HELISEXUAL,
    ANDROGYNY, GENDERLESS, BIGENDER, GENDERQUEER, NOT_SURE_GENDER,
    ROBOT, FAT_DOG, CHARMANDER, CHARMELEON, CHARIZARD;

    public static Gender getGenderByNumber(int i) {
        return Gender.values()[i];
    }

    public String getName() {
        String name = this.name();
        name = name.substring(0, 1) + name.substring(1).toLowerCase();
        name = name.replaceAll("_", " ");
        return name;
    }

    public static void printPostulate() {
        System.out.println("\t\t** There are only 15 genders. **");
    }

    public static Gender getGenderByName(String name) {
        try {
            return Gender.valueOf(name.toUpperCase().replaceAll("\\s", "_"));
        }
        catch(IllegalArgumentException exc) {
            throw new GenderNotFoundException(name);
        }
    }
}
