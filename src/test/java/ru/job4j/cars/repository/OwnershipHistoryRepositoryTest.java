package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OwnershipHistoryRepositoryTest {

    @Autowired private OwnershipHistoryRepository historyRepository;
    @Autowired private OwnershipPeriodRepository periodRepository;
    @Autowired private CarRepository carRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EngineRepository engineRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;
    @Autowired private CarTypeRepository carTypeRepository;
    @Autowired private SessionFactory sessionFactory;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM OwnershipHistory").executeUpdate();
            session.createMutationQuery("DELETE FROM OwnershipPeriod").executeUpdate();
            session.createMutationQuery("DELETE FROM Car").executeUpdate();
            session.createMutationQuery("DELETE FROM Owner").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.createMutationQuery("DELETE FROM Engine").executeUpdate();
            session.createMutationQuery("DELETE FROM Brand").executeUpdate();
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.createMutationQuery("DELETE FROM CarType").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private OwnershipHistory prepareOwnershipHistory() {
        User user = createUser();
        Owner owner = createOwner(user);
        Engine engine = createEngine();
        Brand brand = createBrand();
        Category category = createCategory();
        CarType type = createCarType(category);
        Car car = createCar(owner, brand, category, type, engine);
        OwnershipPeriod period = createPeriod();
        return createHistory(car, owner, period);
    }

    private User createUser() {
        User user = new User();
        user.setLogin("user_" + System.nanoTime());
        user.setPassword("123");
        userRepository.create(user);
        return user;
    }

    private Owner createOwner(User user) {
        Owner owner = new Owner();
        owner.setName("Владелец");
        owner.setUser(user);
        ownerRepository.create(owner);
        return owner;
    }

    private Engine createEngine() {
        Engine engine = new Engine();
        engine.setName("V8");
        engineRepository.create(engine);
        return engine;
    }

    private Brand createBrand() {
        Brand brand = new Brand();
        brand.setName("Toyota");
        brandRepository.create(brand);
        return brand;
    }

    private Category createCategory() {
        Category category = new Category();
        category.setName("Легковая");
        categoryRepository.create(category);
        return category;
    }

    private CarType createCarType(Category category) {
        CarType type = new CarType();
        type.setName("Седан");
        type.setCategory(category);
        carTypeRepository.create(type);
        return type;
    }

    private Car createCar(Owner owner, Brand brand, Category category, CarType type, Engine engine) {
        Car car = new Car();
        car.setName("Camry");
        car.setBrand(brand);
        car.setCategory(category);
        car.setCarType(type);
        car.setEngine(engine);
        car.setOwner(owner);
        carRepository.create(car);
        return car;
    }

    private OwnershipPeriod createPeriod() {
        OwnershipPeriod period = new OwnershipPeriod();
        period.setStartAt(LocalDateTime.now().minusDays(30));
        period.setEndAt(LocalDateTime.now());
        periodRepository.create(period);
        return period;
    }

    private OwnershipHistory createHistory(Car car, Owner owner, OwnershipPeriod period) {
        OwnershipHistoryId id = new OwnershipHistoryId(car.getId(), owner.getId(), period.getId());
        OwnershipHistory history = new OwnershipHistory(id, car, owner, period);
        historyRepository.create(history);
        return history;
    }

    @Test
    void whenCreateThenFindById() {
        var history = prepareOwnershipHistory();
        var result = historyRepository.findById(history.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getCar().getName()).isEqualTo("Camry");
    }

    @Test
    void whenFindAllThenReturnList() {
        prepareOwnershipHistory();
        var result = historyRepository.findAll();
        assertThat(result).isNotEmpty();
    }

    @Test
    void whenFindByCarIdThenReturnHistoryList() {
        var history = prepareOwnershipHistory();
        var result = historyRepository.findByCarId(history.getCar().getId());
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getOwner().getName()).isEqualTo("Владелец");
    }

    @Test
    void whenDeleteThenEntryIsRemoved() {
        var history = prepareOwnershipHistory();
        historyRepository.deleteById(history.getId());
        var result = historyRepository.findById(history.getId());
        assertThat(result).isEmpty();
    }
}
