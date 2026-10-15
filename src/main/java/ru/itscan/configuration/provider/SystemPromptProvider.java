package ru.itscan.configuration.provider;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class SystemPromptProvider {

    private final ResourceLoader resourceLoader;

    @Value("${spring.ai.prompt-path}")
    private String promptPath;

    private String cachePrompt;

    @PostConstruct
    void init() {
        Resource resource = resourceLoader.getResource(promptPath);
        try {
            cachePrompt = resource.getContentAsString(StandardCharsets.UTF_8);
        }
        catch (IOException e) {
            throw new UncheckedIOException("Не удалось загрузить системный промпт", e);
        }
    }

    public String getPrompt() {
        return cachePrompt;
    }
}
