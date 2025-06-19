package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.PriceHistoryRepository;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PriceHistoryServiceTest {

    private PriceHistoryRepository priceHistoryRepository;
    private PriceHistoryService priceHistoryService;

    @BeforeEach
    void setUp() {
        priceHistoryRepository = mock(PriceHistoryRepository.class);
        priceHistoryService = new PriceHistoryService(priceHistoryRepository);
    }

    @Test
    void whenCreateThenReturnPriceHistory() {
        PriceHistory ph = new PriceHistory();
        when(priceHistoryRepository.create(ph)).thenReturn(ph);

        PriceHistory result = priceHistoryService.create(ph);

        assert (result == ph);
        verify(priceHistoryRepository).create(ph);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        PriceHistory ph = new PriceHistory();
        priceHistoryService.update(ph);

        verify(priceHistoryRepository).update(ph);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        priceHistoryService.delete(42);

        verify(priceHistoryRepository).delete(42);
    }

    @Test
    void whenCreateAndSavePriceHistoryThenRepositoryCreateCalled() {
        Post post = new Post();
        BigDecimal before = BigDecimal.valueOf(100);
        BigDecimal after = BigDecimal.valueOf(120);

        priceHistoryService.createAndSavePriceHistory(post, before, after);

        verify(priceHistoryRepository).create(any(PriceHistory.class));
    }
}
