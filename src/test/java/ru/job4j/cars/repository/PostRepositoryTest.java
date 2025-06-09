package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EngineRepository engineRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Post createPost() {
        User user = new User();
        user.setLogin("testUser_" + System.nanoTime());
        user.setPassword("pwd");
        userRepository.create(user);

        Engine engine = new Engine();
        engine.setName("V6");
        engineRepository.create(engine);

        Car car = new Car();
        car.setName("Toyota");
        car.setEngine(engine);
        carRepository.create(car);

        Post post = new Post();
        post.setDescription("Nice car");
        post.setCreated(LocalDateTime.now());
        post.setUser(user);
        post.setCar(car);
        return postRepository.create(post);
    }

    private Post createPostWithCreationDate(LocalDateTime created) {
        Post post = createPost();
        post.setCreated(created);
        postRepository.update(post);
        return post;
    }

    @AfterEach
    void clearTables() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM File").executeUpdate();
            session.createMutationQuery("DELETE FROM Post").executeUpdate();
            session.createMutationQuery("DELETE FROM Car").executeUpdate();
            session.createMutationQuery("DELETE FROM Engine").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreatePostThenItIsPersisted() {
        Post post = createPost();
        assertThat(post.getId()).isPositive();

        Optional<Post> found = postRepository.findAllFromLastDay().stream()
                .filter(p -> p.getId() == post.getId())
                .findFirst();

        assertThat(found).isPresent();
        assertThat(found.get().getDescription()).isEqualTo(post.getDescription());
    }

    @Test
    void whenUpdatePostThenChangesAreSaved() {
        Post post = createPost();
        post.setDescription("Updated description");
        postRepository.update(post);

        Optional<Post> updated = postRepository.findAllFromLastDay().stream()
                .filter(p -> p.getId() == post.getId())
                .findFirst();

        assertThat(updated).isPresent();
        assertThat(updated.get().getDescription()).isEqualTo("Updated description");
    }

    @Test
    void whenDeletePostThenItIsRemoved() {
        Post post = createPost();
        postRepository.delete(post.getId());

        List<Post> posts = postRepository.findAllFromLastDay();
        assertThat(posts).noneMatch(p -> p.getId() == post.getId());
    }

    @Test
    void whenFindAllFromLastDayThenOnlyRecentPostsReturned() {
        createPostWithCreationDate(LocalDateTime.now().minusDays(2));
        Post recentPost = createPostWithCreationDate(LocalDateTime.now().minusHours(12));

        List<Post> posts = postRepository.findAllFromLastDay();
        assertThat(posts).extracting(Post::getId).contains(recentPost.getId());
        assertThat(posts).allMatch(p -> p.getCreated().isAfter(LocalDateTime.now().minusDays(1).minusSeconds(1)));
    }

    @Test
    void whenFindAllWithPhotoThenReturnPostsWithFiles() {
        Post postWithoutFiles = createPost();

        Post postWithFiles = createPost();
        File file = new File();
        file.setName("file1.jpg");
        file.setPath("path");
        file.setPost(postWithFiles);

        postWithFiles.getFiles().add(file);
        postRepository.update(postWithFiles);

        List<Post> posts = postRepository.findAllWithPhoto();
        assertThat(posts).extracting(Post::getId).contains(postWithFiles.getId());
        assertThat(posts).extracting(Post::getId).doesNotContain(postWithoutFiles.getId());
    }

    @Test
    void whenFindAllByCarNameThenReturnPosts() {

        Engine engine = new Engine();
        engine.setName("V6");
        engineRepository.create(engine);

        Car car = new Car();
        car.setName("Honda");
        car.setEngine(engine);
        carRepository.create(car);

        Post post1 = createPost();
        post1.setCar(car);
        postRepository.update(post1);

        Post post2 = createPost();

        List<Post> posts = postRepository.findAllByCarName("Honda");
        assertThat(posts).extracting(Post::getId).contains(post1.getId());
        assertThat(posts).extracting(Post::getId).doesNotContain(post2.getId());
    }
}
