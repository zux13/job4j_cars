package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class EngineRepository {
    private final CrudRepository crudRepository;

    public Engine create(Engine engine) {
        crudRepository.run(session -> session.persist(engine));
        return engine;
    }

    public void update(Engine engine) {
        crudRepository.run(session -> session.merge(engine));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Engine where id =:id",
                Map.of("id", id)
        );
    }

    public List<Engine> findAllOrderById() {
        return crudRepository.query("from Engine order by id asc", Engine.class);
    }

    public Optional<Engine> findById(int id) {
        return crudRepository.optional(
                "from Engine where id = :id", Engine.class,
                Map.of("id", id)
        );
    }

    public List<Engine> findByName(String name) {
        return crudRepository.query(
                "from Engine where name = :name order by id asc",
                Engine.class,
                Map.of("name", name)
        );
    }
}
