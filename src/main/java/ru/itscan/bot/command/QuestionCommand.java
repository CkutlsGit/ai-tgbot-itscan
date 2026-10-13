package ru.itscan.bot.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.itscan.bot.command.enums.CommandName;
import ru.itscan.bot.command.interfaces.Command;
import ru.itscan.bot.configuration.BotProperties;
import ru.itscan.bot.entity.User;
import ru.itscan.bot.enums.UserState;
import ru.itscan.bot.event.MessageEvent;
import ru.itscan.bot.repository.UserRepository;
import ru.itscan.bot.service.UserStateService;
import ru.itscan.dto.AskDto;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuestionCommand implements Command {

    private final ApplicationEventPublisher eventPublisher;
    private final UserStateService userStateService;
    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final BotProperties botProperties;

    private User user;

    @Override
    public boolean canHandle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return false;

        Long chatId = update.getMessage().getChatId();
        user = userRepository.getUserById(update.getMessage().getChatId());
        return userStateService.getState(chatId) == UserState.WAITING_FOR_QUESTION && user.getCountRequest() > 0;
    }

    @Override
    @Async("botExecutor")
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        SendMessage messageBot = SendMessage.builder()
                .chatId(update.getMessage().getChatId())
                .text("Думаю...")
                .build();
        eventPublisher.publishEvent(new MessageEvent(this, messageBot));

        String answer = botProperties.baseAnswerRequest();
        try {
            answer = restTemplate.postForObject(
                    botProperties.askUrl(),
                    new AskDto(text),
                    String.class
            );
        }
        catch (Exception e) {
            log.error("Ошибка при обработке запроса {}", e.getMessage());
        }

        eventPublisher.publishEvent(new MessageEvent(
                this,
                SendMessage.builder()
                        .chatId(chatId)
                        .text(answer)
                        .build()
        ));
        userStateService.clear(chatId);
        if (!answer.equals(botProperties.baseAnswerRequest())) {
            user.setCountRequest(user.getCountRequest() - 1);
        }
    }

    @Override
    public String getCommand() {
        return CommandName.ASK.getName();
    }
}
