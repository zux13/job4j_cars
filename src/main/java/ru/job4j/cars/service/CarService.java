package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.PostEditDto;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.CarRepository;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final EngineService engineService;
    private final CategoryService categoryService;
    private final CarTypeService carTypeService;
    private final BrandService brandService;

    public Car create(Car car) {
        return carRepository.create(car);
    }

    public void update(Car car) {
        carRepository.update(car);
    }

    public void delete(int id) {
        carRepository.delete(id);
    }

    public Car findById(int id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Авто с id " + id + " не найдено"));
    }

    public void updateCarAndEngineFrom(Car car, PostEditDto dto, Owner currentOwner) {

        engineService.updateEngineFrom(car.getEngine(), dto.getEngine());

        car.setName(dto.getCarName());
        car.setCategory(categoryService.findById(dto.getCategoryId()));
        car.setCarType(carTypeService.findById(dto.getTypeId()));
        car.setBrand(brandService.findById(dto.getBrandId()));
        car.setOwner(currentOwner);

        update(car);
    }

    public Car createAndSaveCarAndEngine(PostCreateDto dto, Owner currentOwner) {

        Engine engine = engineService.createAndSaveEngineFrom(dto);

        Car car = new Car();
        car.setName(dto.getCarName());
        car.setCategory(categoryService.findById(dto.getCategoryId()));
        car.setCarType(carTypeService.findById(dto.getTypeId()));
        car.setBrand(brandService.findById(dto.getBrandId()));
        car.setOwner(currentOwner);
        car.setEngine(engine);

        return create(car);
    }

}
