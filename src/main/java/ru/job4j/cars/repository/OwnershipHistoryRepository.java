package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.OwnershipHistory;
import ru.job4j.cars.model.OwnershipHistoryId;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class OwnershipHistoryRepository {
    private final CrudRepository crudRepository;

    public OwnershipHistory create(OwnershipHistory ownershipHistory) {
        crudRepository.run(session -> session.persist(ownershipHistory));
        return ownershipHistory;
    }

    public void update(OwnershipHistory ownershipHistory) {
        crudRepository.run(session -> session.merge(ownershipHistory));
    }

    public void deleteById(OwnershipHistoryId id) {
        crudRepository.run(
                "delete from OwnershipHistory where id =:id",
                Map.of("id", id)
        );
    }

    public List<OwnershipHistory> findAll() {
        return crudRepository.query("from OwnershipHistory order by id asc", OwnershipHistory.class);
    }

    public List<OwnershipHistory> findByCarId(int carId) {
        return crudRepository.query(
                "from OwnershipHistory o Where o.car.id=:carId order by o.id asc",
                OwnershipHistory.class,
                Map.of("carId", carId)
        );
    }

    public Optional<OwnershipHistory> findById(OwnershipHistoryId id) {
        return crudRepository.optional(
                "FROM OwnershipHistory WHERE id=:id",
                OwnershipHistory.class,
                Map.of("id", id)
        );
    }

}
