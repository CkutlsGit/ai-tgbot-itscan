package ru.itscan.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.configuration.BotProperties;
import ru.itscan.bot.entity.User;
import ru.itscan.bot.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CommandHandler {

    private final Collection<Command> commands;
    private final UserRepository userRepository;
    private final BotProperties botProperties;

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
        if (!userRepository.existsUserById(chatId)) {
            userRepository.save(new User(chatId, botProperties.baseCountRequest()));
        }
    }
}
