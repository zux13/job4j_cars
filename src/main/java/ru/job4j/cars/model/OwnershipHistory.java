package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "history_owners")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnershipHistory {

    @EmbeddedId
    private OwnershipHistoryId id;

    @ManyToOne
    @MapsId("carId")
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @MapsId("ownerId")
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne
    @MapsId("historyId")
    @JoinColumn(name = "history_id", nullable = false)
    private OwnershipPeriod period;
}
