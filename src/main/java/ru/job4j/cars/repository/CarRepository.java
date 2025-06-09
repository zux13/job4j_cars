package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class CarRepository {
    private final CrudRepository crudRepository;

    public Car create(Car car) {
        crudRepository.run(session -> session.persist(car));
        return car;
    }

    public void update(Car car) {
        crudRepository.run(session -> session.merge(car));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Car where id =:id",
                Map.of("id", id)
        );
    }

    public List<Car> findAll() {
        return crudRepository.query(
                """
                FROM Car c
                LEFT JOIN FETCH c.currentOwner
                LEFT JOIN FETCH c.owners
                ORDER BY c.id
                """,
                Car.class
        );
    }

    public Optional<Car> findById(int id) {
        return crudRepository.optional(
            """
                   FROM Car c
                   LEFT JOIN FETCH c.currentOwner
                   LEFT JOIN FETCH c.owners
                   WHERE c.id=:id
                   """,
                Car.class,
                Map.of("id", id)
        );
    }

    public List<Car> findByName(String name) {
        return crudRepository.query(
            """
                   FROM Car c
                   LEFT JOIN FETCH c.currentOwner
                   LEFT JOIN FETCH c.owners
                   WHERE c.name=:name
                   """,
                Car.class,
                Map.of("name", name)
        );
    }

}
