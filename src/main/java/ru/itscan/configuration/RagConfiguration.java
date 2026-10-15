package ru.itscan.configuration;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.itscan.configuration.property.RagProperties;

@Configuration
public class RagConfiguration {

    @Bean
    public QuestionAnswerAdvisor advisor(VectorStore vectorStore, RagProperties properties) {
        return QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder()
                        .topK(properties.topK())
                        .similarityThreshold(properties.similarityThreshold())
                        .build())
                .build();
    }
}
