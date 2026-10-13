package ru.itscan.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.service.CommandHandler;
import ru.itscan.bot.service.PreCheckoutQueryHandler;
import ru.itscan.bot.service.SuccessfulPaymentHandle;

@Component
@RequiredArgsConstructor
public class MainBot implements LongPollingSingleThreadUpdateConsumer {

    private final CommandHandler commandHandler;
    private final PreCheckoutQueryHandler preCheckoutQueryHandler;
    private final SuccessfulPaymentHandle successfulPaymentHandle;

    @Override
    public void consume(Update update) {
        if (update.hasPreCheckoutQuery()) {
            preCheckoutQueryHandler.handle(update.getPreCheckoutQuery());
            return;
        }

        if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            successfulPaymentHandle.handle(update);
            return;
        }

        commandHandler.handle(update);
    }
}
