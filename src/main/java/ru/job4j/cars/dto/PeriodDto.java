package ru.job4j.cars.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PeriodDto {
    private LocalDate startAt;
    private LocalDate endAt;
}
