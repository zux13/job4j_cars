package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class BrandRepository {
    private CrudRepository crudRepository;

    public Brand create(Brand brand) {
        crudRepository.run(session -> session.persist(brand));
        return brand;
    }

    public void update(Brand brand) {
        crudRepository.run(session -> session.merge(brand));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Brand where id =:id",
                Map.of("id", id)
        );
    }

    public List<Brand> findAll() {
        return crudRepository.query("FROM Brand b ORDER BY b.id", Brand.class);
    }

    public Optional<Brand> findById(int id) {
        return crudRepository.optional(
                "FROM Brand WHERE id=:id",
                Brand.class,
                Map.of("id", id)
        );
    }
}
