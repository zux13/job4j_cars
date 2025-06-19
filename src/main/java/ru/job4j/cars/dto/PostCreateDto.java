package ru.job4j.cars.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateDto {
    private String carName;
    private int categoryId;
    private int brandId;
    private int typeId;
    private String engine;
    private String description;
    private BigDecimal price;
    private List<MultipartFile> photos = new ArrayList<>();
    private List<OwnershipHistoryDto> ownershipHistories = new ArrayList<>();
}
