package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.enums.CommandName;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.enums.UserState;
import ru.itscan.bot.event.MessageEvent;
import ru.itscan.bot.service.UserStateService;
import ru.itscan.dto.AskDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;
    private final UserStateService userStateService;
    private final RestTemplate restTemplate;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;

        Long chatId = update.getMessage().getChatId();
        return userStateService.getState(chatId) == UserState.WAITING_FOR_QUESTION;
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        SendMessage messageBot = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Думаю...")
                .build();
        eventPublisher.publishEvent(new MessageEvent(this, messageBot));

        String answer = "Не удалось получить ответ";
        try {
            answer = restTemplate.postForObject(
                    "http://localhost:8080/api/v1/rag/ask",
                    new AskDto(text),
                    String.class
            );
        }
        catch (Exception e) {
            log.error("Ошибка при обработке запроса {}", e);
        }

        eventPublisher.publishEvent(new MessageEvent(
                this,
                SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build()
        ));
        userStateService.clear(chatId);
    }

    @Override
    public String getCommand() {
        return CommandName.ASK.getName();
    }
}
