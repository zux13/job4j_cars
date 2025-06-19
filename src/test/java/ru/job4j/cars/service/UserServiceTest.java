package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void whenCreateUserThenReturnOptionalUser() {
        User user = new User();
        user.setLogin("login");
        when(userRepository.create(user)).thenReturn(Optional.of(user));

        Optional<User> result = userService.create(user);

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("login");
        verify(userRepository).create(user);
    }

    @Test
    void whenCreateUserWithExistingLoginThenReturnEmptyOptional() {
        User user = new User();
        user.setLogin("existing");
        when(userRepository.create(user)).thenReturn(Optional.empty());

        Optional<User> result = userService.create(user);

        assertThat(result).isEmpty();
        verify(userRepository).create(user);
    }

    @Test
    void whenFindByLoginAndPasswordThenReturnUserOptional() {
        String login = "user1";
        String password = "pass1";
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        when(userRepository.findByLoginAndPassword(login, password)).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByLoginAndPassword(login, password);

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo(login);
        verify(userRepository).findByLoginAndPassword(login, password);
    }

    @Test
    void whenFindByLoginAndPasswordNotFoundThenReturnEmpty() {
        String login = "noUser";
        String password = "noPass";
        when(userRepository.findByLoginAndPassword(login, password)).thenReturn(Optional.empty());

        Optional<User> result = userService.findByLoginAndPassword(login, password);

        assertThat(result).isEmpty();
        verify(userRepository).findByLoginAndPassword(login, password);
    }
}
