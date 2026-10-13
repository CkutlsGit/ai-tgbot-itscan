package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.invoices.SendInvoice;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import ru.itscan.bot.command.enums.CommandName;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.event.MessageEvent;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuyCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().equals("/buy");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        SendInvoice invoice = SendInvoice.builder()
                .chatId(chatId)
                .title("Покупка +1 запроса")
                .description("Добавление +1 запроса")
                .payload("buy_request_" + chatId)
                .currency("XTR")
                .prices(List.of(new LabeledPrice("+1 запрос", 1)))
                .providerToken("x")
                .build();

        eventPublisher.publishEvent(new MessageEvent(this, invoice));
    }

    @Override
    public String getCommand() {
        return CommandName.BUY.getName();
    }
}
