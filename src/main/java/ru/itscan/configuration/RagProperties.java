package ru.itscan.configuration;

public record RagProperties(
        int topK,
        float similarityThreshold
) {
}
