package ru.itscan.bot.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventsListener {

    private final TelegramClient telegramClient;

    @Async("botExecutor")
    @EventListener
    public void on(MessageEvent event) {
        BotApiMethod<? extends Serializable> message = event.getMessage();
        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Не удалось отправить сообщение в телеге", e);
        }
    }
}
