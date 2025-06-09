package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "car")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private int id;
    private String name;

    @OneToOne
    @JoinColumn(name = "engine_id", foreignKey = @ForeignKey(name = "ENGINE_ID_FK"))
    private Engine engine;

    @Formula("(SELECT ho.owner_id FROM history_owners ho "
            + "JOIN history h ON ho.history_id = h.id "
            + "WHERE ho.car_id = id AND h.startAt <= NOW() AND (h.endAt IS NULL OR h.endAt > NOW()) LIMIT 1)")
    private Long currentOwnerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currentOwnerId", insertable = false, updatable = false)
    private Owner currentOwner;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "history_owners",
            joinColumns = {@JoinColumn(name = "car_id", nullable = false, updatable = false)},
            inverseJoinColumns = {@JoinColumn(name = "owner_id", nullable = false, updatable = false)}
    )
    private Set<Owner> owners = new HashSet<>();
}
