package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.EngineRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EngineServiceTest {

    private EngineRepository engineRepository;
    private EngineService engineService;

    @BeforeEach
    void setUp() {
        engineRepository = mock(EngineRepository.class);
        engineService = new EngineService(engineRepository);
    }

    @Test
    void whenCreateThenReturnEngine() {
        Engine engine = new Engine(0, "V6");
        when(engineRepository.create(engine)).thenReturn(engine);

        Engine result = engineService.create(engine);

        assertThat(result).isEqualTo(engine);
        verify(engineRepository).create(engine);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        Engine engine = new Engine(1, "V8");
        engineService.update(engine);

        verify(engineRepository).update(engine);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        engineService.delete(42);
        verify(engineRepository).delete(42);
    }

    @Test
    void whenCreateAndSaveEngineFromDtoThenReturnEngine() {
        PostCreateDto dto = new PostCreateDto();
        dto.setEngine("Electric");

        Engine expected = new Engine();
        expected.setName("Electric");

        when(engineRepository.create(any(Engine.class))).thenReturn(expected);

        Engine result = engineService.createAndSaveEngineFrom(dto);

        assertThat(result.getName()).isEqualTo("Electric");
        verify(engineRepository).create(any(Engine.class));
    }

    @Test
    void whenUpdateEngineFromThenEngineNameIsUpdatedAndSaved() {
        Engine engine = new Engine(10, "OldName");

        engineService.updateEngineFrom(engine, "NewName");

        assertThat(engine.getName()).isEqualTo("NewName");
        verify(engineRepository).update(engine);
    }
}
