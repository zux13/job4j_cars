package ru.job4j.cars.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.OwnershipHistoryDto;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.OwnerRepository;

import java.util.NoSuchElementException;

@Service
@AllArgsConstructor
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public Owner create(Owner owner) {
        return ownerRepository.create(owner);
    }

    public void update(Owner owner) {
        ownerRepository.update(owner);
    }

    public void delete(int id) {
        ownerRepository.delete(id);
    }

    public Owner findById(int id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Владелец с id " + id + " не найден"));
    }

    public Owner getOrCreateCurrentOwnerFrom(OwnershipHistoryDto dto, User user) {
        Integer ownerId = dto.getId().getOwnerId();
        if (ownerId == null) {
            return createAndSaveOwner(dto, user);
        } else {
            return findById(ownerId);
        }
    }

    public Owner createAndSaveOwner(OwnershipHistoryDto dto, User user) {
        Owner owner = new Owner();
        owner.setName(dto.getOwner().getName());
        owner.setUser(user);
        return create(owner);
    }

    public void updateOwnersNameFrom(Owner owner, OwnershipHistoryDto dto) {
        owner.setName(dto.getOwner().getName());
        update(owner);
    }
}
