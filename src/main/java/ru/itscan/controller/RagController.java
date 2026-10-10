package ru.itscan.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final String SYSTEM_PROMPT = "Ты полезный ИИ-Ассистент для анализа ситуации в IT сфере исходя из данных. Отвечай в формате: анализ данных, отвечая пользователю исходя из анализа, давать рекомендации. Только на русском языке ты говоришь";

    public RagController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
    }

    @PostMapping("/ask")
    public String askModel(@RequestBody String prompt) {
        QuestionAnswerAdvisor advisor =QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(30)
                        .similarityThreshold(0.5)
                        .build()
                )
                .build();

        return chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(prompt)
                .advisors(advisor)
                .call()
                .content();
    }
}
