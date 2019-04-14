package com.bobisonfire.foodshell.commands;

/**
 * Функциональный интерфейс для создания анонимных классов для запуска команд
 * (является одним из полей объектов класса Command).
 */
public interface Launchable {
    void start(CommandDoc launcher, String[] tokens);
}
