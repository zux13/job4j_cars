package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.OwnershipHistoryDto;
import ru.job4j.cars.dto.PostEditDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.OwnershipHistoryRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class OwnershipHistoryService {
    private final OwnershipHistoryRepository ownershipHistoryRepository;
    private final OwnershipPeriodService ownershipPeriodService;
    private final OwnerService ownerService;

    public OwnershipHistory create(OwnershipHistory ownershipHistory) {
        return ownershipHistoryRepository.create(ownershipHistory);
    }

    public void delete(OwnershipHistoryId id) {
        ownershipHistoryRepository.deleteById(id);
    }

    public OwnershipHistory findById(OwnershipHistoryId id) {
        return ownershipHistoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("OwnershipHistory с id " + id + " не найдена"));
    }

    public List<OwnershipHistory> findByCarId(int carId) {
        return ownershipHistoryRepository.findByCarId(carId);
    }

    public OwnershipHistoryDto findCurrentOrLastOwnership(List<OwnershipHistoryDto> ownerships) {

        List<OwnershipHistoryDto> openEndedOwnerships = ownerships.stream()
                .filter(o -> o.getPeriod().getEndAt() == null)
                .toList();

        if (openEndedOwnerships.size() == 1) {
            return openEndedOwnerships.getFirst();
        }

        List<OwnershipHistoryDto> lastOwnerships = ownerships.stream()
                .sorted(Comparator.comparing(o -> o.getPeriod().getEndAt(),
                        Comparator.nullsFirst(Comparator.reverseOrder())))
                .toList();

        return lastOwnerships.getFirst();
    }

    public void updateOwnershipHistoryFrom(PostEditDto dto,
                                           OwnershipHistoryDto currentOwnership,
                                           Owner currentOwner,
                                           User user,
                                           Car car) {

        List<OwnershipHistoryDto> newOwnerships = new ArrayList<>();
        List<OwnershipHistoryDto> existingOwnerships = new ArrayList<>();

        for (var ownership : dto.getOwnershipHistories()) {
            if (ownership.getId().getHistoryId() == null) {
                newOwnerships.add(ownership);
            } else {
                existingOwnerships.add(ownership);
            }
        }

        createAndSaveOwnershipHistoryPeriodsAndOwners(currentOwnership, newOwnerships, user, car, currentOwner);
        updateOwnershipHistoryPeriodsAndOwners(existingOwnerships);
        deleteOwnershipHistoryPeriodsAndOwners(dto.getRemovedOwnersIds());
    }

    public void createAndSaveOwnershipHistoryPeriodsAndOwners(OwnershipHistoryDto currentOwnership,
                                                              List<OwnershipHistoryDto> ownerships,
                                                              User user,
                                                              Car car,
                                                              Owner currentOwner) {

        for (OwnershipHistoryDto ownership : ownerships) {

            Owner owner = ownership.equals(currentOwnership)
                    ? currentOwner
                    : ownerService.createAndSaveOwner(ownership, user);

            OwnershipPeriod period = ownershipPeriodService.createAndSaveOwnershipPeriod(
                    ownership.getPeriod().getStartAt(),
                    ownership.getPeriod().getEndAt()
            );

            createAndSaveOwnershipHistory(car, owner, period);
        }
    }

    private void createAndSaveOwnershipHistory(Car car, Owner owner, OwnershipPeriod period) {
        OwnershipHistory history = new OwnershipHistory();
        history.setId(new OwnershipHistoryId(car.getId(), owner.getId(), period.getId()));
        history.setCar(car);
        history.setOwner(owner);
        history.setPeriod(period);

        create(history);
    }

    public void updateOwnershipHistoryPeriodsAndOwners(List<OwnershipHistoryDto> ownerships) {
        for (var ownership : ownerships) {
            OwnershipHistory history = findById(ownership.getId());
            OwnershipPeriod period = history.getPeriod();
            Owner owner = history.getOwner();

            ownerService.updateOwnersNameFrom(owner, ownership);
            ownershipPeriodService.updateOwnershipPeriodFrom(period, ownership.getPeriod());
        }
    }

    private void deleteOwnershipHistoryPeriodsAndOwners(List<OwnershipHistoryId> historyIds) {
        for (var id : historyIds) {
            OwnershipHistory history = findById(id);
            OwnershipPeriod period = history.getPeriod();
            Owner owner = history.getOwner();

            delete(id);
            ownerService.delete(owner.getId());
            ownershipPeriodService.delete(period.getId());
        }
    }

}
