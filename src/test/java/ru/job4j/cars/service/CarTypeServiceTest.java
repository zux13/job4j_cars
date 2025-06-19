package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.CarTypeDto;
import ru.job4j.cars.model.CarType;
import ru.job4j.cars.model.Category;
import ru.job4j.cars.repository.CarTypeRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CarTypeServiceTest {

    private CarTypeRepository carTypeRepository;
    private CarTypeService carTypeService;

    @BeforeEach
    void setUp() {
        carTypeRepository = mock(CarTypeRepository.class);
        carTypeService = new CarTypeService(carTypeRepository);
    }

    @Test
    void whenCreateThenReturnCreatedType() {
        CarType carType = new CarType(0, "Купе", new Category());
        when(carTypeRepository.create(carType)).thenReturn(carType);

        CarType result = carTypeService.create(carType);

        assertThat(result).isEqualTo(carType);
        verify(carTypeRepository).create(carType);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        CarType carType = new CarType();
        carTypeService.update(carType);

        verify(carTypeRepository).update(carType);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        carTypeService.delete(42);
        verify(carTypeRepository).delete(42);
    }

    @Test
    void whenFindByIdAndExistsThenReturnType() {
        CarType type = new CarType(1, "Седан", new Category());
        when(carTypeRepository.findById(1)).thenReturn(Optional.of(type));

        CarType result = carTypeService.findById(1);

        assertThat(result).isEqualTo(type);
    }

    @Test
    void whenFindByIdAndMissingThenThrow() {
        when(carTypeRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carTypeService.findById(99))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Тип с id 99 не найден");
    }

    @Test
    void whenFindAllThenReturnDtos() {
        Category category = new Category(2, "Легковая");
        CarType t1 = new CarType(1, "Седан", category);
        CarType t2 = new CarType(2, "Купе", category);

        when(carTypeRepository.findAll()).thenReturn(List.of(t1, t2));

        List<CarTypeDto> result = carTypeService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(CarTypeDto::getName)
                .containsExactly("Седан", "Купе");
        assertThat(result)
                .extracting(CarTypeDto::getCategoryId)
                .containsOnly(2);
    }
}
