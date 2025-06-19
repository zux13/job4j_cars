package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.PeriodDto;
import ru.job4j.cars.model.OwnershipPeriod;
import ru.job4j.cars.repository.OwnershipPeriodRepository;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OwnershipPeriodServiceTest {

    private OwnershipPeriodRepository repository;
    private OwnershipPeriodService service;

    @BeforeEach
    void setUp() {
        repository = mock(OwnershipPeriodRepository.class);
        service = new OwnershipPeriodService(repository);
    }

    @Test
    void whenCreateThenReturnSavedOwnershipPeriod() {
        OwnershipPeriod period = new OwnershipPeriod();
        period.setStartAt(LocalDate.now().atStartOfDay());

        when(repository.create(any())).thenReturn(period);

        OwnershipPeriod result = service.create(period);

        assertThat(result).isEqualTo(period);
        verify(repository).create(period);
    }

    @Test
    void whenUpdateThenRepositoryUpdateCalled() {
        OwnershipPeriod period = new OwnershipPeriod();
        service.update(period);
        verify(repository).update(period);
    }

    @Test
    void whenDeleteThenRepositoryDeleteCalled() {
        service.delete(10);
        verify(repository).delete(10);
    }

    @Test
    void whenCreateAndSaveOwnershipPeriodThenReturnsCreated() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 12, 31);

        OwnershipPeriod period = new OwnershipPeriod();
        period.setStartAt(startDate.atStartOfDay());
        period.setEndAt(endDate.atStartOfDay());

        when(repository.create(any())).thenReturn(period);

        OwnershipPeriod result = service.createAndSaveOwnershipPeriod(startDate, endDate);

        assertThat(result.getStartAt()).isEqualTo(startDate.atStartOfDay());
        assertThat(result.getEndAt()).isEqualTo(endDate.atStartOfDay());
        verify(repository).create(any());
    }

    @Test
    void whenUpdateOwnershipPeriodFromThenFieldsAreUpdated() {
        OwnershipPeriod period = new OwnershipPeriod();
        PeriodDto dto = new PeriodDto();
        dto.setStartAt(LocalDate.of(2022, 5, 1));
        dto.setEndAt(LocalDate.of(2022, 5, 10));

        service.updateOwnershipPeriodFrom(period, dto);

        assertThat(period.getStartAt()).isEqualTo(dto.getStartAt().atStartOfDay());
        assertThat(period.getEndAt()).isEqualTo(dto.getEndAt().atStartOfDay());
        verify(repository).update(period);
    }

    @Test
    void whenUpdateOwnershipPeriodFromWithNullEndDateThenEndDateNotSet() {
        OwnershipPeriod period = new OwnershipPeriod();
        PeriodDto dto = new PeriodDto();
        dto.setStartAt(LocalDate.of(2022, 6, 1));
        dto.setEndAt(null);

        service.updateOwnershipPeriodFrom(period, dto);

        assertThat(period.getStartAt()).isEqualTo(dto.getStartAt().atStartOfDay());
        assertThat(period.getEndAt()).isNull();
        verify(repository).update(period);
    }
}
