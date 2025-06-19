package ru.job4j.cars.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class OwnershipHistoryId implements Serializable {

    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "owner_id")
    private Integer ownerId;

    @Column(name = "history_id")
    private Integer historyId;
}
