package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Category;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class CategoryRepository {

    private CrudRepository crudRepository;

    public Category create(Category category) {
        crudRepository.run(session -> session.persist(category));
        return category;
    }

    public void update(Category category) {
        crudRepository.run(session -> session.merge(category));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Category where id =:id",
                Map.of("id", id)
        );
    }

    public List<Category> findAll() {
        return crudRepository.query("FROM Category ORDER BY id", Category.class);
    }

    public Optional<Category> findById(int id) {
        return crudRepository.optional(
                "FROM Category WHERE id=:id",
                Category.class,
                Map.of("id", id)
        );
    }
}
