package ru.itscan.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.interfaces.Command;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommandHandler {

    private final Collection<Command> commands;
    private final UserService userService;

    public void handle(Update update) {
        if (!update.hasMessage()) return;

        registerUserIfAbsent(update.getMessage().getChatId());

        for (Command command : commands) {
            if (command.canHandle(update)) {
                command.handle(update);
                return;
            }
        }
    }

    private void registerUserIfAbsent(Long chatId) {
        if (!userService.existsUserById(chatId)) {
            userService.createUser(chatId);
        }
    }
}
