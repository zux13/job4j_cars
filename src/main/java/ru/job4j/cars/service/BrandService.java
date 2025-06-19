package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.BrandRepository;

import java.util.List;
import java.util.NoSuchElementException;

@AllArgsConstructor
@Service
public class BrandService {
    private final BrandRepository brandRepository;

    public Brand create(Brand brand) {
        return brandRepository.create(brand);
    }

    public void update(Brand brand) {
        brandRepository.update(brand);
    }

    public void delete(int id) {
        brandRepository.delete(id);
    }

    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    public Brand findById(int id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Бренд с id " + id + " не найден"));
    }
}
