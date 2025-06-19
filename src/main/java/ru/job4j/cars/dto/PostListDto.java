package ru.job4j.cars.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PostListDto {
    private int id;
    private int userId;
    private String carName;
    private String engineName;
    private BigDecimal price;
    private boolean sold;
    private String imageBase64;
}
