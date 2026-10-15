package ru.itscan.bot.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import ru.itscan.bot.event.MessageEvent;
import ru.itscan.bot.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class SuccessfulPaymentHandler {

    private final UserService userService;
    private final ApplicationEventPublisher eventPublisher;

    public void handle(Update update) {
        if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            SuccessfulPayment payment = update.getMessage().getSuccessfulPayment();
            String payload = payment.getInvoicePayload();

            if (payload != null && payload.startsWith("buy_request_")) {
                Long chatId;

                try {
                    chatId = Long.parseLong(payload.replace("buy_request_", ""));
                }
                catch (NumberFormatException e) {
                    log.error("Неверный формат айди", e);
                    return;
                }

                userService.incrementUserRequest(chatId);

                SendMessage confirm = SendMessage.builder()
                        .chatId(chatId)
                        .text("Оплата получена! Запрос добавлен")
                        .build();
                eventPublisher.publishEvent(new MessageEvent(this, confirm));
            }
        }
    }
}
