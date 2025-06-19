package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.job4j.cars.model.Post;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
    private String name;
    private Post post;
    private byte[] content;
}
