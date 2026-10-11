package ru.itscan.bot.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
public class EventsListener {

    private final TelegramClient telegramClient;

    @EventListener
    public void on(MessageEvent event) throws TelegramApiException {
        BotApiMethod<? extends Serializable> message = event.getMessage();
        telegramClient.execute(message);
    }
}
