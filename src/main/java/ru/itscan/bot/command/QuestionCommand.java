package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.configuration.property.BotProperties;
import ru.itscan.bot.entity.enums.UserState;
import ru.itscan.bot.event.MessageEvent;
import ru.itscan.bot.service.UserService;
import ru.itscan.service.RagService;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;
    private final RagService ragService;
    private final UserService userService;
    private final BotProperties botProperties;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;

        return userService.getStateUserById(update.getMessage().getChatId()) == UserState.WAITING_FOR_QUESTION;
    }

    @Override
    @Async("botExecutor")
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        eventPublisher.publishEvent(new MessageEvent(
                this,
                SendMessage.builder()
                        .chatId(update.getMessage().getChatId())
                        .text("Думаю...")
                        .build()
        ));

        String answer;
        boolean success = false;
        try {
            answer = ragService.askModel(text);
            success = answer != null && !answer.isBlank();
        }
        catch (Exception e) {
            log.error("Ошибка при обработке запроса", e);
            answer = botProperties.baseAnswerRequest();
        }

        eventPublisher.publishEvent(new MessageEvent(
                this,
                SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build()
        ));

        userService.setStatusUser(chatId, UserState.IDLE);
        if (success) {
            userService.decrementUserRequest(chatId);
        }
    }
}
