package ru.itscan.bot.configuration.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram.bot")
public record BotProperties(
        String baseAnswerRequest,
        String baseStartMessage,
        int baseCountRequest
) {
}
