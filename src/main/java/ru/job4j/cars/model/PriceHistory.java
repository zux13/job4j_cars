package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "before_price")
    private BigDecimal beforePrice;

    @Column(name = "after_price")
    private BigDecimal afterPrice;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "auto_post_id")
    private Post post;
}
