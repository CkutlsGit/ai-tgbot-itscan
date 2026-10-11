package ru.itscan.bot.configuration;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.itscan.bot.MainBot;

@Configuration
public class BotConfiguration {

    private TelegramBotsLongPollingApplication botsLongPollingApplication;

    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot.token}") String token) {
        return new OkHttpTelegramClient(token);
    }

    @Bean
    public TelegramBotsLongPollingApplication telegramBotsLongPollingApplication(@Value("${telegram.bot.token}") String token, MainBot mainBot) throws Exception {
        botsLongPollingApplication = new TelegramBotsLongPollingApplication();
        botsLongPollingApplication.registerBot(token, mainBot);

        return botsLongPollingApplication;
    }

    @PreDestroy
    public void shutdown() throws Exception {
        if (botsLongPollingApplication != null) {
            botsLongPollingApplication.close();
        }
    }
}
