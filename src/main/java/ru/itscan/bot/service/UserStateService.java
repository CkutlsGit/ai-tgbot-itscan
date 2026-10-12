package ru.itscan.bot.service;

import org.springframework.stereotype.Service;
import ru.itscan.bot.enums.UserState;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserStateService {

    private final Map<Long, UserState> states = new ConcurrentHashMap<>();

    public UserState getState(Long chatId) {
        return states.getOrDefault(chatId, UserState.IDLE);
    }

    public void setStates(Long chatId, UserState state) {
        states.put(chatId, state);
    }

    public void clear(Long chatId) {
        states.remove(chatId);
    }
}
