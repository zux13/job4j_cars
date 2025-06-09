package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class OwnerRepository {
    private final CrudRepository crudRepository;

    public Owner create(Owner owner) {
        crudRepository.run(session -> session.persist(owner));
        return owner;
    }

    public void update(Owner owner) {
        crudRepository.run(session -> session.merge(owner));
    }

    public void delete(int id) {
        crudRepository.run(
                "delete from Owner where id =:id",
                Map.of("id", id)
        );
    }

    public List<Owner> findAllOrderById() {
        return crudRepository.query("from Owner order by id asc", Owner.class);
    }

    public Optional<Owner> findById(int id) {
        return crudRepository.optional(
                "from Owner where id = :id", Owner.class,
                Map.of("id", id)
        );
    }

    public List<Owner> findByUserId(int userId) {
        return crudRepository.query(
                "from Owner where user_id = :userId order by id asc",
                Owner.class,
                Map.of("userId", userId)
        );
    }

    public List<Owner> findByName(String name) {
        return crudRepository.query(
                "from Owner where name = :name order by id asc",
                Owner.class,
                Map.of("name", name)
        );
    }

}
