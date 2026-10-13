package ru.itscan.bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itscan.bot.configuration.BotProperties;
import ru.itscan.bot.entity.User;
import ru.itscan.bot.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BotProperties botProperties;

    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Transactional
    public void createUser(Long id) {
        userRepository.save(new User(id, botProperties.baseCountRequest()));
    }

    @Transactional
    public void createUser(Long id, Integer quantity) {
        userRepository.save(new User(id, quantity));
    }

    @Transactional
    public void incrementUserRequest(Long id) {
        User user = userRepository.getUserById(id);

        user.setCountRequest(user.getCountRequest() + 1);
    }

    @Transactional
    public void incrementUserRequest(Long id, Integer quantity) {
        User user = userRepository.getUserById(id);

        user.setCountRequest(user.getCountRequest() + quantity);
    }

    @Transactional
    public void decrementUserRequest(Long id) {
        User user = userRepository.getUserById(id);

        user.setCountRequest(user.getCountRequest() - 1);
    }

    @Transactional
    public void decrementUserRequest(Long id, Integer quantity) {
        User user = userRepository.getUserById(id);

        user.setCountRequest(user.getCountRequest() - quantity);
    }

    @Transactional(readOnly = true)
    public boolean existsUserById(Long id) {
        return userRepository.existsUserById(id);
    }
}
