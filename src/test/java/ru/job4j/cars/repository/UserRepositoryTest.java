package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private User createUniqueUser() {
        User user = new User();
        user.setLogin("test_" + System.nanoTime());
        user.setPassword("pwd");
        return userRepository.create(user);
    }

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreateUserThenItIsPersisted() {
        User user = createUniqueUser();
        assertThat(user.getId()).isPositive();

        Optional<User> found = userRepository.findById(user.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    void whenUpdateUserThenChangesAreSaved() {
        User user = createUniqueUser();
        user.setPassword("new_password");
        userRepository.update(user);

        Optional<User> updated = userRepository.findById(user.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getPassword()).isEqualTo("new_password");
    }

    @Test
    void whenDeleteUserThenItIsRemoved() {
        User user = createUniqueUser();
        userRepository.delete(user.getId());

        Optional<User> deleted = userRepository.findById(user.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void whenFindByIdThenReturnCorrectUser() {
        User user = createUniqueUser();
        Optional<User> result = userRepository.findById(user.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    void whenFindAllOrderByIdThenReturnAllUsers() {
        User u1 = createUniqueUser();
        User u2 = createUniqueUser();

        List<User> users = userRepository.findAllOrderById();
        assertThat(users).anyMatch(u -> u.getId() == u1.getId());
        assertThat(users).anyMatch(u -> u.getId() == u2.getId());
    }

    @Test
    void whenFindByLoginThenReturnCorrectUser() {
        User user = createUniqueUser();

        Optional<User> result = userRepository.findByLogin(user.getLogin());
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(user.getId());
    }

    @Test
    void whenFindByLikeLoginThenReturnMatchingUsers() {
        String baseLogin = "like_login_" + System.nanoTime();
        for (int i = 0; i < 3; i++) {
            User user = new User();
            user.setLogin(baseLogin + "_user" + i);
            user.setPassword("pwd");
            userRepository.create(user);
        }

        List<User> matches = userRepository.findByLikeLogin(baseLogin);
        assertThat(matches).hasSize(3);
        assertThat(matches)
                .extracting(User::getLogin)
                .allMatch(login -> login.startsWith(baseLogin));
    }
}
