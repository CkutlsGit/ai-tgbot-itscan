package ru.itscan.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import ru.itscan.bot.event.MessageEvent;

@Service
@RequiredArgsConstructor
public class PreCheckoutQueryHandler {

    private final ApplicationEventPublisher eventPublisher;

    public void handle(PreCheckoutQuery query) {
        if (query == null || query.getId() == null || query.getId().isBlank()) {
            return;
        }

        String payload = query.getInvoicePayload();
        if (payload == null || !payload.startsWith("buy_request_")) {
            AnswerPreCheckoutQuery reject = AnswerPreCheckoutQuery.builder()
                    .preCheckoutQueryId(query.getId())
                    .ok(false)
                    .errorMessage("Неккоретный платеж")
                    .build();

            eventPublisher.publishEvent(new MessageEvent(this, reject));
            return;
        }

        AnswerPreCheckoutQuery answer = AnswerPreCheckoutQuery.builder()
                .preCheckoutQueryId(query.getId())
                .ok(true)
                .build();

        eventPublisher.publishEvent(new MessageEvent(this, answer));
    }
}
