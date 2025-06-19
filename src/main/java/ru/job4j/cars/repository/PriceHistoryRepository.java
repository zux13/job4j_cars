package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.PriceHistory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class PriceHistoryRepository {
    private final CrudRepository crudRepository;

    public PriceHistory create(PriceHistory priceHistory) {
        crudRepository.run(session -> session.persist(priceHistory));
        return priceHistory;
    }

    public void update(PriceHistory priceHistory) {
        crudRepository.run(session -> session.merge(priceHistory));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from PriceHistory where id =:id",
                Map.of("id", id)
        );
    }

    public List<PriceHistory> findAll() {
        return crudRepository.query("from PriceHistory order by id asc", PriceHistory.class);
    }

    public Optional<PriceHistory> findById(int id) {
        return crudRepository.optional(
                "from PriceHistory where id = :id", PriceHistory.class,
                Map.of("id", id)
        );
    }
}
