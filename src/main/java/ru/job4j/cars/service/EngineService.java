package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.EngineRepository;

@AllArgsConstructor
@Service
public class EngineService {
    private final EngineRepository engineRepository;

    public Engine create(Engine engine) {
        return engineRepository.create(engine);
    }

    public void update(Engine engine) {
        engineRepository.update(engine);
    }

    public void delete(int id) {
        engineRepository.delete(id);
    }

    public Engine createAndSaveEngineFrom(PostCreateDto dto) {
        Engine engine = new Engine();
        engine.setName(dto.getEngine());
        return create(engine);
    }

    public void updateEngineFrom(Engine engine, String name) {
        engine.setName(name);
        update(engine);
    }
}
