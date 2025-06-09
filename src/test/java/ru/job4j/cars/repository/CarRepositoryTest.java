package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private EngineRepository engineRepository;

    @Autowired
    private SessionFactory sessionFactory;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM Car").executeUpdate();
            session.createMutationQuery("DELETE FROM Engine").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreateThenFindById() {
        Engine engine = new Engine();
        engine.setName("V8");
        engineRepository.create(engine);

        Car car = new Car();
        car.setName("Toyota");
        car.setEngine(engine);

        carRepository.create(car);

        Optional<Car> result = carRepository.findById(car.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Toyota");
        assertThat(result.get().getEngine()).isNotNull();
        assertThat(result.get().getEngine().getName()).isEqualTo("V8");
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Engine engine = new Engine();
        engine.setName("V6");
        engineRepository.create(engine);

        Car car = new Car();
        car.setName("Honda");
        car.setEngine(engine);
        carRepository.create(car);

        car.setName("Honda Updated");
        carRepository.update(car);

        Optional<Car> updated = carRepository.findById(car.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Honda Updated");
    }

    @Test
    void whenDeleteThenNotFound() {
        Engine engine = new Engine();
        engine.setName("I4");
        engineRepository.create(engine);

        Car car = new Car();
        car.setName("Ford");
        car.setEngine(engine);
        carRepository.create(car);

        carRepository.delete(car.getId());

        Optional<Car> result = carRepository.findById(car.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void whenFindAllThenGetList() {
        Engine engine1 = new Engine();
        engine1.setName("V12");
        engineRepository.create(engine1);

        Engine engine2 = new Engine();
        engine2.setName("Electric");
        engineRepository.create(engine2);

        Car car1 = new Car();
        car1.setName("Ferrari");
        car1.setEngine(engine1);
        carRepository.create(car1);

        Car car2 = new Car();
        car2.setName("Tesla");
        car2.setEngine(engine2);
        carRepository.create(car2);

        List<Car> cars = carRepository.findAll();
        assertThat(cars).isNotEmpty();
        assertThat(cars).extracting(Car::getName).contains("Ferrari", "Tesla");
    }

    @Test
    void whenFindByNameThenGetCars() {
        Engine engine = new Engine();
        engine.setName("Hybrid");
        engineRepository.create(engine);

        Car car = new Car();
        car.setName("Prius");
        car.setEngine(engine);
        carRepository.create(car);

        List<Car> cars = carRepository.findByName("Prius");
        assertThat(cars).isNotEmpty();
        assertThat(cars.getFirst().getName()).isEqualTo("Prius");

        List<Car> emptyList = carRepository.findByName("NonExistingName");
        assertThat(emptyList).isEmpty();
    }
}

