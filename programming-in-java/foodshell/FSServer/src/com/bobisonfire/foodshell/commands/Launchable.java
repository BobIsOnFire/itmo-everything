package com.bobisonfire.foodshell.commands;

import com.bobisonfire.foodshell.Connection;

/**
 * Функциональный интерфейс для создания анонимных классов для запуска команд
 * (является одним из полей объектов класса Command).
 */
public interface Launchable {
    boolean start(Connection con, String[] tokens);
}
