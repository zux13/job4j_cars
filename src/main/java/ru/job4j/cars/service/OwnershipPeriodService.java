package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.PeriodDto;
import ru.job4j.cars.model.OwnershipPeriod;
import ru.job4j.cars.repository.OwnershipPeriodRepository;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class OwnershipPeriodService {
    private final OwnershipPeriodRepository ownershipPeriodRepository;

    public OwnershipPeriod create(OwnershipPeriod ownershipPeriod) {
        return ownershipPeriodRepository.create(ownershipPeriod);
    }

    public void update(OwnershipPeriod ownershipPeriod) {
        ownershipPeriodRepository.update(ownershipPeriod);
    }

    public void delete(int id) {
        ownershipPeriodRepository.delete(id);
    }

    public OwnershipPeriod createAndSaveOwnershipPeriod(LocalDate startDate, LocalDate endDate) {
        OwnershipPeriod period = new OwnershipPeriod();
        period.setStartAt(startDate.atStartOfDay());
        if (endDate != null) {
            period.setEndAt(endDate.atStartOfDay());
        }
        return create(period);
    }

    public void updateOwnershipPeriodFrom(OwnershipPeriod period, PeriodDto periodDto) {
        period.setStartAt(periodDto.getStartAt().atStartOfDay());
        if (periodDto.getEndAt() != null) {
            period.setEndAt(periodDto.getEndAt().atStartOfDay());
        }
        update(period);
    }
}
