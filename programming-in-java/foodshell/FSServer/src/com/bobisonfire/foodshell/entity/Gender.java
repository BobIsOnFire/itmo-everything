package com.bobisonfire.foodshell.entity;

import com.bobisonfire.foodshell.exc.NotFoundException;

/**
 * Enum из всех (нет) гендеров. Организует быстрый доступ к ним через методы получения гендера по
 * имени или порядковому номеру плюс логики обработки отстутствия соответствующих гендеров.
 */
public enum Gender {
    MALE, FEMALE, CUTE_CAT, SHREK, GENDERFLUID_HELISEXUAL,
    ANDROGYNY, GENDERLESS, BIGENDER, GENDERQUEER, NOT_SURE_GENDER,
    ROBOT, FAT_DOG, CHARMANDER, CHARMELEON, CHARIZARD;

    public static Gender getGenderByNumber(int i) {
        return Gender.values()[i];
    }

    /**
     * Возвращает более сносное название гендера, чем соответствующее ему название
     * синглтона: например, GENDERFLUID_HELISEXUAL меняется на Genderfluid helisexual.<br>
     * В таком формате консольные команды читают и пишут гендеры в <i>FoodShell</i>.
     */
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
            throw new NotFoundException("Гендер не найден: " + name);
        }
    }
}
