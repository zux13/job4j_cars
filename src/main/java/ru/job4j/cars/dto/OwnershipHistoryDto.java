package ru.job4j.cars.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.job4j.cars.model.OwnershipHistoryId;

@Data
@EqualsAndHashCode
public class OwnershipHistoryDto {
    private OwnershipHistoryId id;
    private OwnerDto owner;
    private PeriodDto period;
}
