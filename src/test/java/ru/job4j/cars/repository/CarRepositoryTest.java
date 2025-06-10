package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CarRepositoryTest {

    @Autowired private CarRepository carRepository;
    @Autowired private EngineRepository engineRepository;
    @Autowired private SessionFactory sessionFactory;

    @Autowired private BrandRepository brandRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private CarTypeRepository carTypeRepository;
    @Autowired private OwnerRepository ownerRepository;
    @Autowired private UserRepository userRepository;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
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

        CarType type = new CarType();
        type.setName("Седан");
        type.setCategory(category);
        carTypeRepository.create(type);

        Car car = new Car();
        car.setName(name);
        car.setEngine(engine);
        car.setBrand(brand);
        car.setCategory(category);
        car.setCarType(type);
        car.setOwner(owner);

        return car;
    }

    @Test
    void whenCreateThenFindById() {
        Car car = prepareCar("Toyota", "V8");
        carRepository.create(car);

        Optional<Car> result = carRepository.findById(car.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Toyota");
        assertThat(result.get().getEngine().getName()).isEqualTo("V8");
        assertThat(result.get().getBrand().getName()).isEqualTo("Toyota");
        assertThat(result.get().getCategory().getName()).isEqualTo("Легковая");
        assertThat(result.get().getCarType().getName()).isEqualTo("Седан");
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Car car = prepareCar("Honda", "V6");
        carRepository.create(car);

        car.setName("Honda Updated");
        carRepository.update(car);

        Optional<Car> updated = carRepository.findById(car.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Honda Updated");
    }

    @Test
    void whenDeleteThenNotFound() {
        Car car = prepareCar("Ford", "I4");
        carRepository.create(car);

        carRepository.delete(car.getId());

        Optional<Car> result = carRepository.findById(car.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void whenFindAllThenGetList() {
        Car car1 = prepareCar("Ferrari", "V12");
        carRepository.create(car1);

        Car car2 = prepareCar("Tesla", "Electric");
        carRepository.create(car2);

        List<Car> cars = carRepository.findAll();
        assertThat(cars).extracting(Car::getName).contains("Ferrari", "Tesla");
    }

    @Test
    void whenFindByNameThenGetCars() {
        Car car = prepareCar("Prius", "Hybrid");
        carRepository.create(car);

        List<Car> cars = carRepository.findByName("Prius");
        assertThat(cars).isNotEmpty();
        assertThat(cars.getFirst().getName()).isEqualTo("Prius");

        List<Car> emptyList = carRepository.findByName("NonExistingName");
        assertThat(emptyList).isEmpty();
    }
}
