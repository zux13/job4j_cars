package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.CarType;
import ru.job4j.cars.model.Category;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CarTypeRepositoryTest {

    @Autowired private CarTypeRepository carTypeRepository;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SessionFactory sessionFactory;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM CarType").executeUpdate();
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private CarType prepareCarType(String name) {
        Category category = new Category();
        category.setName("Легковая");
        categoryRepository.create(category);

        CarType type = new CarType();
        type.setName(name);
        type.setCategory(category);
        return type;
    }

    @Test
    void whenCreateThenFindById() {
        CarType type = prepareCarType("Седан");
        carTypeRepository.create(type);

        Optional<CarType> result = carTypeRepository.findById(type.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Седан");
        assertThat(result.get().getCategory().getName()).isEqualTo("Легковая");
    }

    @Test
    void whenUpdateThenGetUpdated() {
        CarType type = prepareCarType("Кроссовер");
        carTypeRepository.create(type);

        type.setName("Кроссовер XL");
        carTypeRepository.update(type);

        Optional<CarType> updated = carTypeRepository.findById(type.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Кроссовер XL");
    }

    @Test
    void whenDeleteThenNotFound() {
        CarType type = prepareCarType("Хэтчбек");
        carTypeRepository.create(type);

        carTypeRepository.delete(type.getId());

        Optional<CarType> deleted = carTypeRepository.findById(type.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void whenFindAllThenGetListInOrder() {
        CarType type1 = prepareCarType("Седан");
        CarType type2 = prepareCarType("Универсал");

        carTypeRepository.create(type1);
        carTypeRepository.create(type2);

        List<CarType> types = carTypeRepository.findAll();
        assertThat(types).hasSize(2);
        assertThat(types).extracting(CarType::getName)
                .containsExactly(type1.getName(), type2.getName());
    }

    @Test
    void whenFindByIdNotExistsThenEmpty() {
        Optional<CarType> result = carTypeRepository.findById(-1);
        assertThat(result).isEmpty();
    }
}
