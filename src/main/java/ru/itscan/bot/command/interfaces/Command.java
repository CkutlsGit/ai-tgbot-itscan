package ru.itscan.bot.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    boolean canHandle(Update update);
    void handle(Update update);
    String getCommand();
}
