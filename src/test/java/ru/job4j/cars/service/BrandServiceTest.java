package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Brand;
import ru.job4j.cars.repository.BrandRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BrandServiceTest {

    private BrandRepository brandRepository;
    private BrandService brandService;

    @BeforeEach
    void setUp() {
        brandRepository = mock(BrandRepository.class);
        brandService = new BrandService(brandRepository);
    }

    @Test
    void whenCreateThenReturnsSavedBrand() {
        Brand brand = new Brand();
        brand.setName("Toyota");

        when(brandRepository.create(brand)).thenReturn(brand);

        Brand result = brandService.create(brand);
        assertThat(result).isEqualTo(brand);

        verify(brandRepository).create(brand);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        Brand brand = new Brand();
        brand.setId(1);
        brand.setName("Updated");

        brandService.update(brand);

        verify(brandRepository).update(brand);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        brandService.delete(5);
        verify(brandRepository).delete(5);
    }

    @Test
    void whenFindAllThenReturnList() {
        List<Brand> expected = List.of(new Brand(1, "Audi"), new Brand(2, "BMW"));
        when(brandRepository.findAll()).thenReturn(expected);

        List<Brand> result = brandService.findAll();
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void whenFindByIdThenReturnBrand() {
        Brand brand = new Brand(1, "Kia");
        when(brandRepository.findById(1)).thenReturn(Optional.of(brand));

        Brand result = brandService.findById(1);
        assertThat(result).isEqualTo(brand);
    }

    @Test
    void whenFindByIdAndNotFoundThenThrow() {
        when(brandRepository.findById(42)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.findById(42))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Бренд с id 42 не найден");
    }
}
