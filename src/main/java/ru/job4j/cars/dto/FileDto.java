package ru.job4j.cars.dto;

import lombok.Data;
import ru.job4j.cars.model.Post;

@Data
public class FileDto {
    private String name;
    private Post post;
    private byte[] content;
}
