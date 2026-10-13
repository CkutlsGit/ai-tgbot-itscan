package ru.itscan.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itscan.service.interfaces.Parser;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class RssSchedule {

    private static final List<String> RESOURCES = List.of(
            "https://habr.com/ru/rss/hubs/career/articles/?fl=ru",
            "https://habr.com/ru/rss/hubs/research/articles/?fl=ru",
            "https://habr.com/ru/rss/hubs/startuprise/articles/?fl=ru",
            "https://habr.com/ru/rss/hubs/statistics/articles/?fl=ru"
    );
    private final Parser parser;

    @Scheduled(fixedRateString = "${parser.fixed-rate}", initialDelayString = "${parser.init-delay}")
    public void timeParse() {
      log.info("Start Schedule Parse");

      for (String resource : RESOURCES) {
          parser.parse(resource);
      }

      log.info("End Schedule Parse");
    }
}
