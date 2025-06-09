package ru.job4j.cars.repository;

import org.hibernate.SessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.job4j.cars.model.Engine;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class EngineRepositoryTest {

    @Autowired
    private EngineRepository engineRepository;

    @Autowired
    private SessionFactory sessionFactory;

    @AfterEach
    void clearTables() {
        try (var session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM Engine").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void whenCreateThenFindById() {
        Engine engine = new Engine();
        engine.setName("V8");
        engineRepository.create(engine);

        Optional<Engine> result = engineRepository.findById(engine.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("V8");
    }

    @Test
    void whenUpdateThenGetUpdated() {
        Engine engine = new Engine();
        engine.setName("OldName");
        engineRepository.create(engine);

        engine.setName("NewName");
        engineRepository.update(engine);

        Optional<Engine> result = engineRepository.findById(engine.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("NewName");
    }

    @Test
    void whenDeleteThenNotFound() {
        Engine engine = new Engine();
        engine.setName("ToDelete");
        engineRepository.create(engine);

        engineRepository.delete(engine.getId());

        Optional<Engine> result = engineRepository.findById(engine.getId());
        assertThat(result).isEmpty();
    }

    @Test
    void whenFindAllOrderByIdThenSuccess() {
        Engine engine1 = new Engine();
        engine1.setName("Engine1");
        engineRepository.create(engine1);

        Engine engine2 = new Engine();
        engine2.setName("Engine2");
        engineRepository.create(engine2);

        List<Engine> engines = engineRepository.findAllOrderById();
        assertThat(engines).hasSizeGreaterThanOrEqualTo(2);
        assertThat(engines).extracting(Engine::getName).contains("Engine1", "Engine2");
    }

    @Test
    void whenFindByNameThenGetCorrectEngines() {
        Engine engine = new Engine();
        engine.setName("SearchName");
        engineRepository.create(engine);

        List<Engine> result = engineRepository.findByName("SearchName");
        assertThat(result).isNotEmpty();
        assertThat(result.getFirst().getName()).isEqualTo("SearchName");
    }
}
