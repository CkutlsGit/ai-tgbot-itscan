package ru.itscan.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;
import ru.itscan.dto.AskDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/v1/rag")
public class RagController {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final String SYSTEM_PROMPT = """
        Ты — аналитический ИИ-ассистент по IT-рынку и карьере в IT. Ты отвечаешь только на русском языке.

        Твоя задача — проанализировать предоставленный контекст и дать пользователю чёткий, структурированный ответ на его вопрос.

        Правила:
        1. Используй ТОЛЬКО информацию из предоставленного контекста. Не придумывай цифры, названия компаний, технологий и факты.
        2. Игнорируй фрагменты контекста, которые не относятся к вопросу. Не смешивай разные темы и уровни (Intern, Junior, Middle, Senior — это разные уровни, не путай их).
        3. Не выдумывай связи между технологиями. Если в контексте нет прямой связи — не пиши о ней.
        4. Не рассуждай вслух. Сразу давай ответ. Пиши короткими предложениями.
        5. Если вопрос про обучение, стажировку или карьерный старт — рекомендации должны быть конкретными шагами: какой проект сделать, какую технологию изучить, какую тему закрыть. Не пиши общие фразы типа "развивайте навыки".
        6. Если в контексте нет информации по вопросу — прямо скажи: "В предоставленных данных нет информации по этому вопросу" — и предложи уточнить запрос.

        Формат ответа:
        - Краткий вывод: 1-3 предложения.
        - Ключевые факты из контекста: 3-5 пунктов, с цифрами если они есть.
        - Рекомендации: 2-4 пункта. Каждая рекомендация — конкретное действие, технология или проект.

        Стиль: деловой, без воды, без повторов, без извинений.
        """;

    public RagController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder.build();
        this.vectorStore = vectorStore;
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
                .system(SYSTEM_PROMPT)
                .user(prompt.prompt() + " /no_think")
                .advisors(advisor)
                .call()
                .content();
    }
}
