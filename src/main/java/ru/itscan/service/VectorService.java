package ru.itscan.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public void save(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        List<Document> chunks = splitter.apply(documents);

        if (chunks.isEmpty()) return;

        vectorStore.add(chunks);
    }
}
