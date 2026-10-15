package ru.itscan.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;

import java.io.StringReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RssParserServiceTest {

    @Mock
    private VectorService vectorService;

    private RssParserService parser;

    @BeforeEach
    void setUp() {
        parser = new RssParserService(vectorService);
    }

    @Test
    void parse_ValidRss_SetsUrlMetadata() {
        String rss = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rss version="2.0"><channel>
                  <item>
                    <title>Article</title>
                    <link>https://example.com/article-1</link>
                    <description>Some article body</description>
                  </item>
                </channel></rss>
                """;

        parser.parse(new StringReader(rss));

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorService).save(captor.capture());

        Document document = captor.getValue().get(0);
        assertThat(document.getMetadata()).containsEntry("url", "https://example.com/article-1");
    }

    @Test
    void parse_ItemWithoutLink_DoesNotThrow() {
        String rss = """
                <?xml version="1.0" encoding="UTF-8"?>
                <rss version="2.0"><channel>
                  <item>
                    <title>No link article</title>
                    <description>Some article body</description>
                  </item>
                </channel></rss>
                """;

        parser.parse(new StringReader(rss));

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorService).save(captor.capture());

        assertThat(captor.getValue().get(0).getMetadata().get("url")).isNotNull();
    }

    @Test
    void parse_InvalidXml_SavesNothing() {
        parser.parse(new StringReader("not an rss at all"));

        verifyNoInteractions(vectorService);
    }
}