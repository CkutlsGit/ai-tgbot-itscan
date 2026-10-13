package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.enums.CommandName;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.entity.User;
import ru.itscan.bot.event.MessageEvent;
import ru.itscan.bot.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class ProfileCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals("/profile");
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            User user = userRepository.getUserById(update.getMessage().getChatId());

            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text(user.toString())
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }

    @Override
    public String getCommand() {
        return CommandName.PROFILE.getName();
    }
}
