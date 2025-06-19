package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.PriceHistory;
import ru.job4j.cars.repository.PriceHistoryRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PriceHistoryService {
    private final PriceHistoryRepository priceHistoryRepository;

    public PriceHistory create(PriceHistory priceHistory) {
        return priceHistoryRepository.create(priceHistory);
    }

    public void update(PriceHistory priceHistory) {
        priceHistoryRepository.update(priceHistory);
    }

    public void delete(int id) {
        priceHistoryRepository.delete(id);
    }

    public void createAndSavePriceHistory(Post post, BigDecimal beforePrice, BigDecimal afterPrice) {
        PriceHistory priceHistory = new PriceHistory();
        priceHistory.setBeforePrice(beforePrice);
        priceHistory.setAfterPrice(afterPrice);
        priceHistory.setPost(post);
        priceHistory.setCreated(LocalDateTime.now());
        create(priceHistory);
    }

}
