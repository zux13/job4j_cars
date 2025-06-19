package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.OwnerDto;
import ru.job4j.cars.dto.OwnershipHistoryDto;
import ru.job4j.cars.dto.PeriodDto;
import ru.job4j.cars.dto.PostEditDto;
import ru.job4j.cars.model.*;
import ru.job4j.cars.repository.OwnershipHistoryRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnershipHistoryServiceTest {

    private OwnershipHistoryRepository ownershipHistoryRepository;
    private OwnershipPeriodService ownershipPeriodService;
    private OwnerService ownerService;
    private OwnershipHistoryService ownershipHistoryService;

    @BeforeEach
    void setUp() {
        ownershipHistoryRepository = mock(OwnershipHistoryRepository.class);
        ownershipPeriodService = mock(OwnershipPeriodService.class);
        ownerService = mock(OwnerService.class);
        ownershipHistoryService = new OwnershipHistoryService(
                ownershipHistoryRepository,
                ownershipPeriodService,
                ownerService
        );
    }

    @Test
    void whenCreateThenReturnEntity() {
        OwnershipHistory history = new OwnershipHistory();
        when(ownershipHistoryRepository.create(history)).thenReturn(history);

        OwnershipHistory result = ownershipHistoryService.create(history);

        assertThat(result).isEqualTo(history);
        verify(ownershipHistoryRepository).create(history);
    }

    @Test
    void whenFindByIdExistsThenReturn() {
        OwnershipHistoryId id = new OwnershipHistoryId(1, 2, 3);
        OwnershipHistory history = new OwnershipHistory();
        when(ownershipHistoryRepository.findById(id)).thenReturn(Optional.of(history));

        OwnershipHistory result = ownershipHistoryService.findById(id);

        assertThat(result).isEqualTo(history);
    }

    @Test
    void whenFindByIdNotExistsThenThrow() {
        OwnershipHistoryId id = new OwnershipHistoryId(1, 2, 3);
        when(ownershipHistoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownershipHistoryService.findById(id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("OwnershipHistory с id");
    }

    @Test
    void whenFindCurrentOrLastOwnershipWithOneOpenEndedThenReturnIt() {
        OwnershipHistoryDto dto = new OwnershipHistoryDto();
        PeriodDto period = new PeriodDto();
        period.setStartAt(LocalDate.now().minusYears(1));
        period.setEndAt(null);
        dto.setPeriod(period);

        var result = ownershipHistoryService.findCurrentOrLastOwnership(List.of(dto));

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void whenFindCurrentOrLastOwnershipWithNoOpenThenReturnLatestByEndDate() {
        OwnershipHistoryDto first = new OwnershipHistoryDto();
        PeriodDto period1 = new PeriodDto();
        period1.setStartAt(LocalDate.of(2020, 1, 1));
        period1.setEndAt(LocalDate.of(2021, 1, 1));
        first.setPeriod(period1);

        OwnershipHistoryDto second = new OwnershipHistoryDto();
        PeriodDto period2 = new PeriodDto();
        period2.setStartAt(LocalDate.of(2022, 1, 1));
        period2.setEndAt(LocalDate.of(2023, 1, 1));
        second.setPeriod(period2);

        var result = ownershipHistoryService.findCurrentOrLastOwnership(List.of(first, second));

        assertThat(result).isEqualTo(second);
    }

    @Test
    void whenUpdateOwnershipHistoryFromThenDelegatesCorrectly() {
        OwnershipHistoryId id = new OwnershipHistoryId(1, 2, 100);
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setName("OwnerName");
        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartAt(LocalDate.now().minusYears(1));

        OwnershipHistoryDto existing = new OwnershipHistoryDto();
        existing.setId(id);
        existing.setOwner(ownerDto);
        existing.setPeriod(periodDto);

        PostEditDto dto = new PostEditDto();
        dto.setOwnershipHistories(List.of(existing));
        dto.setRemovedOwnersIds(List.of(id));

        Owner owner = new Owner();
        owner.setId(2);
        OwnershipPeriod period = new OwnershipPeriod();
        period.setId(100);
        OwnershipHistory found = new OwnershipHistory();
        found.setOwner(owner);
        found.setPeriod(period);

        when(ownershipHistoryRepository.findById(id)).thenReturn(Optional.of(found));

        ownershipHistoryService.updateOwnershipHistoryFrom(
                dto, existing, new Owner(), new User(), new Car());

        verify(ownerService).updateOwnersNameFrom(owner, existing);
        verify(ownershipPeriodService).updateOwnershipPeriodFrom(period, periodDto);
        verify(ownershipHistoryRepository).deleteById(id);
        verify(ownerService).delete(2);
        verify(ownershipPeriodService).delete(100);
    }

    @Test
    void whenCreateAndSaveOwnershipHistoryThenRepositoryCalled() {
        Car car = new Car();
        car.setId(1);

        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setName("Test Owner");

        PeriodDto periodDto = new PeriodDto();
        periodDto.setStartAt(LocalDate.now().minusYears(1));
        periodDto.setEndAt(LocalDate.now());

        OwnershipHistoryDto dto = new OwnershipHistoryDto();
        dto.setOwner(ownerDto);
        dto.setPeriod(periodDto);

        Owner owner = new Owner();
        owner.setId(2);

        OwnershipPeriod period = new OwnershipPeriod();
        period.setId(3);

        when(ownerService.createAndSaveOwner(any(), any())).thenReturn(owner);
        when(ownershipPeriodService.createAndSaveOwnershipPeriod(any(), any())).thenReturn(period);
        when(ownershipHistoryRepository.create(any())).thenAnswer(inv -> inv.getArgument(0));

        ownershipHistoryService.createAndSaveOwnershipHistoryPeriodsAndOwners(
                new OwnershipHistoryDto(), List.of(dto), new User(), car, owner
        );

        verify(ownerService).createAndSaveOwner(any(), any());
        verify(ownershipPeriodService).createAndSaveOwnershipPeriod(any(), any());
        verify(ownershipHistoryRepository).create(argThat(h ->
                h.getId().getCarId() == 1
                        && h.getId().getOwnerId() == 2
                        && h.getId().getHistoryId() == 3));
    }

}
