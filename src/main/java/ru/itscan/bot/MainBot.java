package ru.itscan.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class MainBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;

    public MainBot(@Value("${telegram.bot.token}") String token) {
        this.telegramClient = new OkHttpTelegramClient(token);
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage sendMessage = new SendMessage(String.valueOf(update.getMessage().getChatId()),
                    update.getMessage().getText());

            try {
                telegramClient.execute(sendMessage);
            }
            catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
