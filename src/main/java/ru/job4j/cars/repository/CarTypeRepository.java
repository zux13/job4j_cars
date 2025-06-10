package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.CarType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class CarTypeRepository {

    private CrudRepository crudRepository;

    public CarType create(CarType carType) {
        crudRepository.run(session -> session.persist(carType));
        return carType;
    }

    public void update(CarType carType) {
        crudRepository.run(session -> session.merge(carType));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from CarType where id =:id",
                Map.of("id", id)
        );
    }

    public List<CarType> findAll() {
        return crudRepository.query("FROM CarType ORDER BY id", CarType.class);
    }

    public Optional<CarType> findById(int id) {
        return crudRepository.optional(
                "FROM CarType WHERE id=:id",
                CarType.class,
                Map.of("id", id)
        );
    }
}
