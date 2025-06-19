package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.Brand;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BrandRepositoryTest {

    @Autowired private BrandRepository brandRepository;
    @Autowired private SessionFactory sessionFactory;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM Brand").executeUpdate();
            session.getTransaction().commit();
        }
    }

    private Brand prepareBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        return brand;
    }

    @Test
    void whenCreateThenFindById() {
        Brand brand = prepareBrand("Mazda");
        brandRepository.create(brand);

        Optional<Brand> result = brandRepository.findById(brand.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Mazda");
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Brand brand = prepareBrand("Nissan");
        brandRepository.create(brand);

        brand.setName("Nissan Updated");
        brandRepository.update(brand);

        Optional<Brand> updated = brandRepository.findById(brand.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Nissan Updated");
    }

    @Test
    void whenDeleteThenNotFound() {
        Brand brand = prepareBrand("Peugeot");
        brandRepository.create(brand);

        brandRepository.delete(brand.getId());

        Optional<Brand> deleted = brandRepository.findById(brand.getId());
        assertThat(deleted).isEmpty();
    }

    @Test
    void whenFindAllThenGetListInOrder() {
        Brand brand1 = prepareBrand("Volvo");
        Brand brand2 = prepareBrand("BMW");
        brandRepository.create(brand1);
        brandRepository.create(brand2);

        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).hasSize(2);
        assertThat(brands).extracting(Brand::getName)
                .containsExactly(brand1.getName(), brand2.getName());
    }

    @Test
    void whenFindByIdNotExistsThenEmpty() {
        Optional<Brand> result = brandRepository.findById(-1);
        assertThat(result).isEmpty();
    }
}
