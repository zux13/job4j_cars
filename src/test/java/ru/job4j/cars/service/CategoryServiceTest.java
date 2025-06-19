package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Category;
import ru.job4j.cars.repository.CategoryRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void whenCreateThenReturnCategory() {
        Category category = new Category(0, "Легковая");
        when(categoryRepository.create(category)).thenReturn(category);

        Category result = categoryService.create(category);

        assertThat(result).isEqualTo(category);
        verify(categoryRepository).create(category);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        Category category = new Category();
        categoryService.update(category);

        verify(categoryRepository).update(category);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        categoryService.delete(10);
        verify(categoryRepository).delete(10);
    }

    @Test
    void whenFindByIdAndExistsThenReturnCategory() {
        Category category = new Category(5, "Грузовая");
        when(categoryRepository.findById(5)).thenReturn(Optional.of(category));

        Category result = categoryService.findById(5);

        assertThat(result).isEqualTo(category);
    }

    @Test
    void whenFindByIdAndMissingThenThrowException() {
        when(categoryRepository.findById(404)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.findById(404))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Категория с id 404 не найдена");
    }

    @Test
    void whenFindAllThenReturnList() {
        List<Category> expected = List.of(
                new Category(1, "Легковая"),
                new Category(2, "Грузовая")
        );

        when(categoryRepository.findAll()).thenReturn(expected);

        List<Category> result = categoryService.findAll();

        assertThat(result).isEqualTo(expected);
    }
}
