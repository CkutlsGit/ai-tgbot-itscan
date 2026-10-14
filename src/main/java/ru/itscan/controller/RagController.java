package ru.itscan.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import ru.itscan.configuration.SystemPromptProvider;
import ru.itscan.dto.AskDto;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
@Slf4j
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final SystemPromptProvider promptProvider;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore, SystemPromptProvider systemPromptProvider) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
        this.promptProvider = systemPromptProvider;
    }

    @PostMapping("/ask")
    public String askModel(@RequestBody AskDto prompt) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(prompt.prompt())
                        .topK(5)
                        .similarityThreshold(0.7)
                        .build());
        docs.forEach(d -> log.info("CHUNK: {}", d.getText().substring(0, 300)));

        QuestionAnswerAdvisor advisor =QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(5)
                        .similarityThreshold(0.7)
                        .build()
                )
                .build();

        return chatClient.prompt()
                .system(promptProvider.getPrompt())
                .user(prompt.prompt() + " /no_think")
                .advisors(advisor)
                .call()
                .content();
    }
}
