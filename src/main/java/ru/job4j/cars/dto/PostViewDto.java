package ru.job4j.cars.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostViewDto {
    private int id;
    private int userId;
    private String description;
    private LocalDateTime created;
    private BigDecimal previousPrice;
    private BigDecimal currentPrice;
    private String carName;
    private String engineName;
    private String categoryName;
    private String typeName;
    private String brandName;
    private String authorName;
    private int ownersCount;
    private boolean sold;
    private List<String> base64images = new ArrayList<>();
}
