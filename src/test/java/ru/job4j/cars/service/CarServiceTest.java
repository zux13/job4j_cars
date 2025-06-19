package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PostCreateDto;
import ru.job4j.cars.dto.PostEditDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.CarRepository;

import java.util.Optional;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarServiceTest {

    private CarRepository carRepository;
    private EngineService engineService;
    private CategoryService categoryService;
    private CarTypeService carTypeService;
    private BrandService brandService;
    private CarService carService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        engineService = mock(EngineService.class);
        categoryService = mock(CategoryService.class);
        carTypeService = mock(CarTypeService.class);
        brandService = mock(BrandService.class);

        carService = new CarService(
                carRepository, engineService,
                categoryService, carTypeService, brandService
        );
    }

    @Test
    void whenCreateThenCarIsPersisted() {
        Car car = new Car();
        when(carRepository.create(car)).thenReturn(car);

        Car result = carService.create(car);
        assertThat(result).isEqualTo(car);

        verify(carRepository).create(car);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        Car car = new Car();
        carService.update(car);

        verify(carRepository).update(car);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        carService.delete(7);
        verify(carRepository).delete(7);
    }

    @Test
    void whenFindByIdThenReturnCar() {
        Car car = new Car();
        when(carRepository.findById(3)).thenReturn(Optional.of(car));

        Car result = carService.findById(3);
        assertThat(result).isEqualTo(car);
    }

    @Test
    void whenFindByIdAndNotFoundThenThrow() {
        when(carRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carService.findById(99))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Авто с id 99 не найдено");
    }

    @Test
    void whenUpdateCarAndEngineFromDtoThenFieldsAreUpdated() {
        Car car = new Car();
        Engine engine = new Engine();
        car.setEngine(engine);
        car.setName("OldName");

        PostEditDto dto = new PostEditDto();
        dto.setCarName("NewName");
        dto.setEngine("V6");
        dto.setBrandId(1);
        dto.setCategoryId(2);
        dto.setTypeId(3);

        Brand brand = new Brand(1, "BMW");
        Category category = new Category(2, "Легковая");
        CarType carType = new CarType(3, "Седан", category);
        Owner owner = new Owner();
        owner.setId(100);

        when(categoryService.findById(2)).thenReturn(category);
        when(brandService.findById(1)).thenReturn(brand);
        when(carTypeService.findById(3)).thenReturn(carType);

        carService.updateCarAndEngineFrom(car, dto, owner);

        verify(engineService).updateEngineFrom(engine, "V6");
        verify(carRepository).update(car);

        assertThat(car.getName()).isEqualTo("NewName");
        assertThat(car.getOwner()).isEqualTo(owner);
        assertThat(car.getCategory()).isEqualTo(category);
        assertThat(car.getBrand()).isEqualTo(brand);
        assertThat(car.getCarType()).isEqualTo(carType);
    }

    @Test
    void whenCreateAndSaveCarAndEngineThenReturnCar() {
        PostCreateDto dto = new PostCreateDto();
        dto.setCarName("Audi A4");
        dto.setBrandId(1);
        dto.setCategoryId(2);
        dto.setTypeId(3);

        Engine engine = new Engine();
        Brand brand = new Brand(1, "Audi");
        Category category = new Category(2, "Седан");
        CarType carType = new CarType(3, "Купе", category);
        Owner owner = new Owner();

        when(engineService.createAndSaveEngineFrom(dto)).thenReturn(engine);
        when(brandService.findById(1)).thenReturn(brand);
        when(categoryService.findById(2)).thenReturn(category);
        when(carTypeService.findById(3)).thenReturn(carType);

        Car expectedCar = new Car();
        expectedCar.setName("Audi A4");
        expectedCar.setBrand(brand);
        expectedCar.setCategory(category);
        expectedCar.setCarType(carType);
        expectedCar.setEngine(engine);
        expectedCar.setOwner(owner);

        when(carRepository.create(any())).thenAnswer(inv -> inv.getArgument(0));

        Car created = carService.createAndSaveCarAndEngine(dto, owner);

        verify(carRepository).create(any());
        assertThat(created.getName()).isEqualTo("Audi A4");
        assertThat(created.getOwner()).isEqualTo(owner);
        assertThat(created.getBrand()).isEqualTo(brand);
        assertThat(created.getEngine()).isEqualTo(engine);
    }
}
