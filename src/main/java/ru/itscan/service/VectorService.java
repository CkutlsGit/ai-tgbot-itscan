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
    private final  TokenTextSplitter splitter = TokenTextSplitter.builder()
            .withChunkSize(500)
            .withMinChunkSizeChars(100)
            .withMinChunkLengthToEmbed(50)
            .withMaxNumChunks(1000)
            .withKeepSeparator(true)
            .build();

    public void save(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        List<Document> chunks = splitter.apply(documents);

        vectorStore.add(chunks);
    }
}
