package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "history_owners",
            joinColumns = {@JoinColumn(name = "car_id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name = "owner_id", nullable = false)}
    )
    private Set<Owner> owners = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "auto_category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "auto_brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "auto_type_id", nullable = false)
    private CarType carType;
}
