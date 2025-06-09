package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.File;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class FileRepositoryTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Post createAndSavePost() {
        User user = new User();
        user.setLogin("user_" + System.nanoTime());
        user.setPassword("pwd");
        userRepository.create(user);

        Post post = new Post();
        post.setDescription("Test post");
        post.setCreated(LocalDateTime.now());
        post.setUser(user);
        return postRepository.create(post);
    }

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createMutationQuery("DELETE FROM Post").executeUpdate();
            session.createMutationQuery("DELETE FROM File").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenSaveFileThenItIsPersisted() {
        Post post = createAndSavePost();
        File file = new File();
        file.setName("image.png");
        file.setPath("uploads/image.png");
        file.setPost(post);

        Optional<File> saved = fileRepository.save(file);
        assertThat(saved).isPresent();
        assertThat(saved.get().getId()).isPositive();
    }

    @Test
    void whenSaveDuplicatePathThenReturnsEmptyOptional() {
        Post post = createAndSavePost();

        File file1 = new File();
        file1.setName("first.png");
        file1.setPath("unique/path.png");
        file1.setPost(post);
        fileRepository.save(file1);

        File file2 = new File();
        file2.setName("duplicate.png");
        file2.setPath("unique/path.png");
        file2.setPost(post);

        Optional<File> result = fileRepository.save(file2);
        assertThat(result).isEmpty();
    }

    @Test
    void whenUpdateFileThenChangesPersisted() {
        Post post = createAndSavePost();
        File file = new File();
        file.setName("old.png");
        file.setPath("uploads/old.png");
        file.setPost(post);
        fileRepository.save(file);

        file.setName("new.png");
        fileRepository.update(file);

        List<File> files = fileRepository.findByPostId(post.getId());
        assertThat(files).hasSize(1);
        assertThat(files.getFirst().getName()).isEqualTo("new.png");
    }

    @Test
    void whenDeleteFileThenItIsRemoved() {
        Post post = createAndSavePost();
        File file = new File();
        file.setName("to-delete.png");
        file.setPath("uploads/to-delete.png");
        file.setPost(post);
        fileRepository.save(file);

        fileRepository.delete(file.getId());
        List<File> files = fileRepository.findByPostId(post.getId());

        assertThat(files).isEmpty();
    }

    @Test
    void whenFindByPostIdThenReturnAllFiles() {
        Post post = createAndSavePost();

        File file1 = new File();
        file1.setName("f1.png");
        file1.setPath("p1");
        file1.setPost(post);
        fileRepository.save(file1);

        File file2 = new File();
        file2.setName("f2.png");
        file2.setPath("p2");
        file2.setPost(post);
        fileRepository.save(file2);

        List<File> files = fileRepository.findByPostId(post.getId());

        assertThat(files).hasSize(2);
        assertThat(files)
                .extracting(File::getPath)
                .containsExactlyInAnyOrder("p1", "p2");
    }
}
