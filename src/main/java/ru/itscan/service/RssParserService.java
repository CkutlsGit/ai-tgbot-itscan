package ru.itscan.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import ru.itscan.service.interfaces.Parser;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RssParserService implements Parser {

    private final VectorStore vectorStore;

    public void parse(String url) {
        try {
            SyndFeedInput parser = new SyndFeedInput();
            SyndFeed resource = parser.build(new XmlReader(new URL(url)));

            List<Document> articles =resource.getEntries().stream()
                    .map(this::toDocument)
                    .toList();

            save(articles);
        }
        catch (Exception e) {
            log.error("Ошибка при парсинге - {}", e.getMessage());
            log.error(" FULL {} {} {} {}", e.getClass(), e.getMessage(), e.getCause(), e.getStackTrace());
        }
    }

    private void save(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }

        vectorStore.add(documents);
    }

    private Document toDocument(SyndEntry article) {
        String rawContent = article.getContents() != null && !article.getContents().isEmpty() ? article.getContents()
                .stream()
                .map(SyndContent::getValue)
                .collect(Collectors.joining("\n"))
        : (article.getDescription() != null ? article.getDescription().getValue() : "");
        Map<String, Object> metadata = Map.of(
                "url", article.getLink()
        );
        String content = article.getTitle() + Jsoup.parse(rawContent).text();
        String id = UUID.nameUUIDFromBytes(article.getLink().getBytes(StandardCharsets.UTF_8)).toString();

        return new Document(id, content, metadata);
    }
}
