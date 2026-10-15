package ru.itscan.bot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.itscan.bot.configuration.property.BotProperties;
import ru.itscan.bot.entity.User;
import ru.itscan.bot.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BotProperties botProperties;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, botProperties);
    }

    @Test
    void createUser_NewUser_SavedWithBaseCountRequest() {
        when(botProperties.baseCountRequest()).thenReturn(3);

        userService.createUser(1L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getCountRequest()).isEqualTo(3);
    }

    @Test
    void incrementUserRequest_ExistingUser_IncrementsByOne() {
        User user = new User(1L, 3);
        when(userRepository.getUserById(1L)).thenReturn(user);

        userService.incrementUserRequest(1L);

        assertThat(user.getCountRequest()).isEqualTo(4);
    }

    @Test
    void decrementUserRequest_ExistingUser_DecrementsByOne() {
        User user = new User(1L, 3);
        when(userRepository.getUserById(1L)).thenReturn(user);

        userService.decrementUserRequest(1L);

        assertThat(user.getCountRequest()).isEqualTo(2);
    }
}