package ru.itscan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VectorServiceTest {

    @Mock
    private VectorStore vectorStore;

    private VectorService vectorService;

    @BeforeEach
    void setUp() {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(50)
                .withMaxNumChunks(1000)
                .withKeepSeparator(true)
                .build();

        vectorService = new VectorService(vectorStore, splitter);
    }

    @Test
    void save_EmptyList_DoesNotCallVectorStore() {
        vectorService.save(List.of());

        verifyNoInteractions(vectorStore);
    }

    @Test
    void save_ValidDocument_SavesChunkToVectorStore() {
        Document document = new Document(
                "Article about Spring AI. It is a framework for building AI applications in Java.");

        vectorService.save(List.of(document));

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(captor.capture());

        assertThat(captor.getValue())
                .isNotEmpty()
                .allSatisfy(chunk -> assertThat(chunk.getText()).contains("Spring AI"));
    }

    @Test
    void save_TooShortDocument_SkipsEmbedding() {
        Document tinyDocument = new Document("too short");

        vectorService.save(List.of(tinyDocument));

        verifyNoInteractions(vectorStore);
    }
}