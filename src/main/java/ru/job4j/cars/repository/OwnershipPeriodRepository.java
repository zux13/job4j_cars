package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.OwnershipPeriod;

import java.util.List;
import java.util.Map;

@Repository
@AllArgsConstructor
public class OwnershipPeriodRepository {
    private final CrudRepository crudRepository;

    public OwnershipPeriod create(OwnershipPeriod ownershipPeriod) {
        crudRepository.run(session -> session.persist(ownershipPeriod));
        return ownershipPeriod;
    }

    public void update(OwnershipPeriod ownershipPeriod) {
        crudRepository.run(session -> session.merge(ownershipPeriod));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from OwnershipPeriod where id =:id",
                Map.of("id", id)
        );
    }

    public List<OwnershipPeriod> findAll() {
        return crudRepository.query("from OwnershipPeriod order by id asc", OwnershipPeriod.class);
    }

}
