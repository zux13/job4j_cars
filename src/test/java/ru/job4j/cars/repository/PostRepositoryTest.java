package ru.job4j.cars.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.dto.PostFilterDto;
import ru.job4j.cars.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PostRepositoryTest {

    @Autowired private PostRepository postRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EngineRepository engineRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private FileRepository fileRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CarTypeRepository carTypeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private SessionFactory sessionFactory;

    private Car prepareCar(String name, String engineName) {
        User user = new User();
        user.setLogin("user_" + System.nanoTime());
        user.setPassword("pass");
        userRepository.create(user);

        Owner owner = new Owner();
        owner.setName("owner");
        owner.setUser(user);
        ownerRepository.create(owner);

        Engine engine = new Engine();
        engine.setName(engineName);
        engineRepository.create(engine);

        Brand brand = new Brand();
        brand.setName("Toyota");
        brandRepository.create(brand);

        Category category = new Category();
        category.setName("Легковая");
        categoryRepository.create(category);

        CarType carType = new CarType();
        carType.setName("Седан");
        carType.setCategory(category);
        carTypeRepository.create(carType);

        Car car = new Car();
        car.setName(name);
        car.setEngine(engine);
        car.setBrand(brand);
        car.setCategory(category);
        car.setCarType(carType);
        car.setOwner(owner);

        carRepository.create(car);
        return car;
    }

    private Post createPost() {
        User user = new User();
        user.setLogin("testUser_" + System.nanoTime());
        user.setPassword("pwd");
        userRepository.create(user);

        Car car = prepareCar("Toyota", "V6");

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
            session.createMutationQuery("DELETE FROM Brand").executeUpdate();
            session.createMutationQuery("DELETE FROM CarType").executeUpdate();
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.createMutationQuery("DELETE FROM Owner").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreatePostThenItIsPersisted() {
        Post post = createPost();
        assertThat(post.getId()).isPositive();

        Optional<Post> found = postRepository.findAll()
                .stream()
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

        Optional<Post> updated = postRepository.findAll()
                .stream()
                .filter(p -> p.getId() == post.getId())
                .findFirst();

        assertThat(updated).isPresent();
        assertThat(updated.get().getDescription()).isEqualTo("Updated description");
    }

    @Test
    void whenDeletePostThenItIsRemoved() {
        Post post = createPost();
        postRepository.delete(post.getId());

        List<Post> posts = postRepository.findAll();
        assertThat(posts).noneMatch(p -> p.getId() == post.getId());
    }

    @Test
    void whenFindByIdThenReturnPostWithInitializedFields() {
        Post post = createPost();
        File file = new File();
        file.setName("photo.png");
        file.setPath("uploads/photo.png");
        file.setPost(post);
        fileRepository.save(file);

        Optional<Post> found = postRepository.findById(post.getId());
        assertThat(found).isPresent();
        Post result = found.get();

        assertThat(result.getCar()).isNotNull();
        assertThat(result.getUser()).isNotNull();
        assertThat(result.getFiles()).isNotEmpty();
    }

    @Test
    void whenFindByFilterWithMyPostsThenReturnOnlyMine() {
        Post myPost = createPost();
        PostFilterDto filter = new PostFilterDto();
        filter.setOnlyMyPosts(true);
        filter.setUserId(myPost.getUser().getId());
        filter.setLimit(10);
        filter.setOffset(0);

        List<Post> result = postRepository.findByFilter(filter);
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(p -> p.getUser().getId() == myPost.getUser().getId());
    }

    @Test
    void whenFindByFilterWithPhotoThenReturnOnlyWithFiles() {
        Post post = createPost();
        File file = new File();
        file.setName("img.png");
        file.setPath("uploads/img.png");
        file.setPost(post);
        fileRepository.save(file);

        PostFilterDto filter = new PostFilterDto();
        filter.setOnlyWithPhoto(true);
        filter.setLimit(10);
        filter.setOffset(0);

        List<Post> result = postRepository.findByFilter(filter);
        assertThat(result).anyMatch(p -> !p.getFiles().isEmpty());
    }

    @Test
    void whenCountByFilterWithPeriodIdThenCorrectCount() {
        createPostWithCreationDate(LocalDateTime.now().minusDays(2));
        PostFilterDto filter = new PostFilterDto();
        filter.setPeriodId(3); // За неделю
        filter.setLimit(10);
        filter.setOffset(0);

        long count = postRepository.countByFilter(filter);
        assertThat(count).isGreaterThanOrEqualTo(1);
    }

    @Test
    void whenFindByFilterWithBrandIdThenReturnMatching() {
        Post post = createPost();
        PostFilterDto filter = new PostFilterDto();
        filter.setBrandIds(List.of(post.getCar().getBrand().getId()));
        filter.setLimit(10);
        filter.setOffset(0);

        List<Post> result = postRepository.findByFilter(filter);
        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(p ->
                filter.getBrandIds().contains(p.getCar().getBrand().getId()));
    }

}