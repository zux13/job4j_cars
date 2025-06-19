package ru.job4j.cars.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.dto.OwnerDto;
import ru.job4j.cars.dto.OwnershipHistoryDto;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.OwnershipHistoryId;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.OwnerRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OwnerServiceTest {

    private OwnerRepository ownerRepository;
    private OwnerService ownerService;

    @BeforeEach
    void setUp() {
        ownerRepository = mock(OwnerRepository.class);
        ownerService = new OwnerService(ownerRepository);
    }

    @Test
    void whenCreateThenReturnOwner() {
        Owner owner = new Owner();
        when(ownerRepository.create(owner)).thenReturn(owner);

        Owner result = ownerService.create(owner);

        assertThat(result).isEqualTo(owner);
        verify(ownerRepository).create(owner);
    }

    @Test
    void whenUpdateThenRepositoryCalled() {
        Owner owner = new Owner();
        ownerService.update(owner);

        verify(ownerRepository).update(owner);
    }

    @Test
    void whenDeleteThenRepositoryCalled() {
        ownerService.delete(1);
        verify(ownerRepository).delete(1);
    }

    @Test
    void whenFindByIdAndExistsThenReturnOwner() {
        Owner owner = new Owner();
        when(ownerRepository.findById(1)).thenReturn(Optional.of(owner));

        Owner result = ownerService.findById(1);

        assertThat(result).isEqualTo(owner);
    }

    @Test
    void whenFindByIdAndNotExistsThenThrowException() {
        when(ownerRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ownerService.findById(99))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("Владелец с id 99 не найден");
    }

    @Test
    void whenGetOrCreateCurrentOwnerFromWithNullOwnerIdThenCreate() {
        OwnershipHistoryDto dto = new OwnershipHistoryDto();
        dto.setId(new OwnershipHistoryId(1, null, 2));
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setName("Новый владелец");
        dto.setOwner(ownerDto);
        User user = new User();

        Owner newOwner = new Owner();
        when(ownerRepository.create(any())).thenReturn(newOwner);

        Owner result = ownerService.getOrCreateCurrentOwnerFrom(dto, user);

        assertThat(result).isEqualTo(newOwner);
        verify(ownerRepository).create(any());
    }

    @Test
    void whenGetOrCreateCurrentOwnerFromWithIdThenFindExisting() {
        OwnershipHistoryDto dto = new OwnershipHistoryDto();
        dto.setId(new OwnershipHistoryId(1, 5, 2));
        Owner expected = new Owner();
        when(ownerRepository.findById(5)).thenReturn(Optional.of(expected));

        Owner result = ownerService.getOrCreateCurrentOwnerFrom(dto, new User());

        assertThat(result).isEqualTo(expected);
        verify(ownerRepository).findById(5);
    }

    @Test
    void whenUpdateOwnersNameFromThenOwnerUpdated() {
        Owner owner = new Owner();
        owner.setName("Старое имя");

        OwnershipHistoryDto dto = new OwnershipHistoryDto();
        OwnerDto ownerDto = new OwnerDto();
        ownerDto.setName("Новое имя");
        dto.setOwner(ownerDto);

        ownerService.updateOwnersNameFrom(owner, dto);

        assertThat(owner.getName()).isEqualTo("Новое имя");
        verify(ownerRepository).update(owner);
    }
}
