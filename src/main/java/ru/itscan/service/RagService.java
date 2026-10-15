package ru.itscan.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import ru.itscan.configuration.RagProperties;
import ru.itscan.configuration.SystemPromptProvider;

@Service
@Slf4j
@RequiredArgsConstructor
public class RagService {

    private final ChatClient chatClient;
    private final SystemPromptProvider promptProvider;
    private final QuestionAnswerAdvisor advisor;

    public String askModel(String prompt) {
        return chatClient.prompt()
                .system(promptProvider.getPrompt())
                .user(prompt + " /no_think")
                .advisors(advisor)
                .call()
                .content();
    }
}
