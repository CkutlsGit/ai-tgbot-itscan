package ru.itscan.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rag")
public record RagProperties(
        int topK,
        float similarityThreshold
) {
}
