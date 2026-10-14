package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.enums.CommandName;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.enums.UserState;
import ru.itscan.bot.event.MessageEvent;
import ru.itscan.bot.service.UserService;

@Component
@RequiredArgsConstructor
public class AskCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals("/ask")
                && userService.getUserById(update.getMessage().getChatId()).getCountRequest() > 0;
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();

            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text("Внимательно тебя слушаю...")
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, message));
            userService.setStatusUser(chatId, UserState.WAITING_FOR_QUESTION);
        }
    }

    @Override
    public String getCommand() {
        return CommandName.ASK.getName();
    }
}
