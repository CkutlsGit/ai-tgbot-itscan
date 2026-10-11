package ru.itscan.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.service.CommandHandler;

@Component
@RequiredArgsConstructor
public class MainBot implements LongPollingSingleThreadUpdateConsumer {

    private final CommandHandler commandHandler;

    @Override
    public void consume(Update update) {
        commandHandler.handle(update);
    }
}
