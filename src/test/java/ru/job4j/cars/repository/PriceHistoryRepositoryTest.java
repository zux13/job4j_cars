package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PriceHistoryRepositoryTest {

    @Autowired
    private PriceHistoryRepository priceHistoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private User createAndSaveUser() {
        User user = new User();
        user.setLogin("user_" + System.nanoTime());
        user.setPassword("pwd");
        return userRepository.create(user).orElseThrow();
    }

    private Post createAndSavePost() {
        User user = createAndSaveUser();

        Post post = new Post();
        post.setDescription("Test post");
        post.setCreated(LocalDateTime.now());
        post.setUser(user);
        return postRepository.create(post);
    }

    private PriceHistory createPriceHistory(Post post, BigDecimal before, BigDecimal after, LocalDateTime created) {
        PriceHistory ph = new PriceHistory();
        ph.setPost(post);
        ph.setBeforePrice(before);
        ph.setAfterPrice(after);
        ph.setCreated(created);
        return priceHistoryRepository.create(ph);
    }

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM PriceHistory").executeUpdate();
            session.createMutationQuery("DELETE FROM Post").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreateThenFindById() {
        Post post = createAndSavePost();
        LocalDateTime created = LocalDateTime.now().withNano(0);
        PriceHistory ph = createPriceHistory(post, new BigDecimal("1000"), new BigDecimal("900"), created);

        Optional<PriceHistory> found = priceHistoryRepository.findById(ph.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBeforePrice()).isEqualByComparingTo("1000");
        assertThat(found.get().getAfterPrice()).isEqualByComparingTo("900");
        assertThat(found.get().getCreated().withNano(0)).isEqualTo(created);
        assertThat(found.get().getPost().getId()).isEqualTo(post.getId());
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Post post = createAndSavePost();
        LocalDateTime created = LocalDateTime.now().withNano(0);
        PriceHistory ph = createPriceHistory(post, new BigDecimal("1000"), new BigDecimal("900"), created);

        ph.setBeforePrice(new BigDecimal("1200"));
        ph.setAfterPrice(new BigDecimal("1100"));
        ph.setCreated(created.plusDays(1));
        priceHistoryRepository.update(ph);

        Optional<PriceHistory> found = priceHistoryRepository.findById(ph.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getBeforePrice()).isEqualByComparingTo("1200");
        assertThat(found.get().getAfterPrice()).isEqualByComparingTo("1100");
        assertThat(found.get().getCreated().withNano(0)).isEqualTo(created.plusDays(1));
    }

    @Test
    void whenDeleteThenNotFound() {
        Post post = createAndSavePost();
        PriceHistory ph = createPriceHistory(post, BigDecimal.TEN, BigDecimal.ONE, LocalDateTime.now());
        int id = ph.getId();

        priceHistoryRepository.delete(id);

        Optional<PriceHistory> found = priceHistoryRepository.findById(id);
        assertThat(found).isEmpty();
    }

    @Test
    void whenFindAllThenReturnList() {
        Post post = createAndSavePost();
        PriceHistory ph1 = createPriceHistory(post, BigDecimal.ONE, BigDecimal.ZERO, LocalDateTime.now());
        PriceHistory ph2 = createPriceHistory(post, new BigDecimal("500"), new BigDecimal("450"), LocalDateTime.now());

        List<PriceHistory> all = priceHistoryRepository.findAll();

        assertThat(all).extracting(PriceHistory::getId).contains(ph1.getId(), ph2.getId());
    }
}
