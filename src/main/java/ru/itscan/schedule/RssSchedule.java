package ru.itscan.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itscan.service.interfaces.Parser;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RssSchedule {

    private final List<String> RESOURCES = List.of(
            "https://habr.com/ru/rss/hubs/career/articles/?fl=ru",
            "https://habr.com/ru/rss/hubs/research/articles/?fl=ru",
            "https://habr.com/ru/rss/hubs/startuprise/articles/?fl=ru",
            "https://habr.com/ru/rss/hubs/statistics/articles/?fl=ru"
    );
    private final Parser parser;

    @Scheduled(fixedRate = 43_200_000, initialDelay = 25_000)
    public void timeParse() {
      log.info("Start Schedule Parse");

      for (String resource : RESOURCES) {
          parser.parse(resource);
      }

      log.info("End Schedule Parse");
    }
}
