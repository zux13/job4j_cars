package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.Category;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CategoryRepositoryTest {

    @Autowired private CategoryRepository categoryRepository;
    @Autowired private SessionFactory sessionFactory;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM Category").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private Category prepareCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    @Test
    void whenCreateThenFindById() {
        Category category = prepareCategory("Грузовая");
        categoryRepository.create(category);

        Optional<Category> result = categoryRepository.findById(category.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Грузовая");
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Category category = prepareCategory("Микроавтобусы");
        categoryRepository.create(category);

        category.setName("Минивэны");
        categoryRepository.update(category);

        Optional<Category> updated = categoryRepository.findById(category.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Минивэны");
    }

    @Test
    void whenDeleteThenNotFound() {
        Category category = prepareCategory("Пикапы");
        categoryRepository.create(category);

        categoryRepository.delete(category.getId());

        Optional<Category> deleted = categoryRepository.findById(category.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void whenFindAllThenGetListInOrder() {
        Category cat1 = prepareCategory("Седаны");
        Category cat2 = prepareCategory("Купе");
        categoryRepository.create(cat1);
        categoryRepository.create(cat2);

        List<Category> categories = categoryRepository.findAll();
        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName)
                .containsExactly(cat1.getName(), cat2.getName());
    }

    @Test
    void whenFindByIdNotExistsThenEmpty() {
        Optional<Category> result = categoryRepository.findById(-100);
        assertThat(result).isEmpty();
    }
}
