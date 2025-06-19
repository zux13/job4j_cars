package ru.job4j.cars.dto;

import lombok.Data;

@Data
public class CarTypeDto {
    private int id;
    private String name;
    private int categoryId;
}
