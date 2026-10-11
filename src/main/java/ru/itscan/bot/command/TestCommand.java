package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.enums.CommandName;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.event.MessageEvent;

@Component
@RequiredArgsConstructor
public class TestCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals("/test");
    }

    @Override
    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Test command")
                    .build();
            eventPublisher.publishEvent(new MessageEvent(this, message));
        }
    }

    @Override
    public String getCommand() {
        return CommandName.TEST.getName();
    }
}
