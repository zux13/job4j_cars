package ru.job4j.cars.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "auto_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String login;
    private String password;
}
