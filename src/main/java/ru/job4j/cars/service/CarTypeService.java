package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.CarTypeDto;
import ru.job4j.cars.model.CarType;
import ru.job4j.cars.repository.CarTypeRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CarTypeService {
    private final CarTypeRepository carTypeRepository;

    public CarType create(CarType carType) {
        return carTypeRepository.create(carType);
    }

    public void update(CarType carType) {
        carTypeRepository.update(carType);
    }

    public void delete(int id) {
        carTypeRepository.delete(id);
    }

    public CarType findById(int id) {
        return carTypeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Тип с id " + id + " не найден"));
    }

    public List<CarTypeDto> findAll() {
        return carTypeRepository.findAll()
                .stream()
                .map(this::toCarTypeDto)
                .toList();
    }

    private CarTypeDto toCarTypeDto(CarType carType) {
        CarTypeDto dto = new CarTypeDto();
        dto.setId(carType.getId());
        dto.setName(carType.getName());
        dto.setCategoryId(carType.getCategory().getId());
        return dto;
    }
}
