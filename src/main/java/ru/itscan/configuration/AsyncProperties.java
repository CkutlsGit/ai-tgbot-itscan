package ru.itscan.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "async-thread")
public record AsyncProperties(
        int corePoolSize,
        int maxPoolSize,
        int queueCapacity,
        String threadPrefix
) {
}
