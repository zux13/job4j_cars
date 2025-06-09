package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OwnerRepositoryTest {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionFactory sessionFactory;

    private Owner createOwner() {
        User user = new User();
        user.setLogin("owner_test" + System.nanoTime());
        user.setPassword("pwd");
        userRepository.create(user);

        Owner owner = new Owner();
        owner.setName("Owner");
        owner.setUser(user);
        return ownerRepository.create(owner);
    }

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM Owner").executeUpdate();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreateOwnerThenItIsPersisted() {
        Owner owner = createOwner();
        assertThat(owner.getId()).isPositive();

        var found = ownerRepository.findById(owner.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo(owner.getName());
    }

    @Test
    void whenUpdateOwnerThenChangesAreSaved() {
        Owner owner = createOwner();
        owner.setName("Updated_Name");
        ownerRepository.update(owner);

        var updated = ownerRepository.findById(owner.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Updated_Name");
    }

    @Test
    void whenDeleteOwnerThenItIsRemoved() {
        Owner owner = createOwner();
        ownerRepository.delete(owner.getId());

        var result = ownerRepository.findById(owner.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void whenFindAllOrderByIdThenReturnAllOwners() {
        Owner o1 = createOwner();
        Owner o2 = createOwner();

        var owners = ownerRepository.findAllOrderById();
        assertThat(owners).anyMatch(o -> o.getId() == o1.getId());
        assertThat(owners).anyMatch(o -> o.getId() == o2.getId());
    }

    @Test
    void whenFindByIdThenReturnCorrectOwner() {
        Owner owner = createOwner();

        var result = ownerRepository.findById(owner.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo(owner.getName());
    }

    @Test
    void whenFindByUserIdThenReturnOwnerList() {
        Owner owner = createOwner();

        var result = ownerRepository.findByUserId(owner.getUser().getId());
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getUser().getId()).isEqualTo(owner.getUser().getId());
    }

    @Test
    void whenFindByNameThenReturnMatchingOwners() {
        Owner owner = createOwner();

        var found = ownerRepository.findByName(owner.getName());
        assertThat(found).isNotEmpty();
        assertThat(found)
                .extracting(Owner::getName)
                .contains(owner.getName());
    }
}
