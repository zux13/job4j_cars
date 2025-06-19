package ru.job4j.cars.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.cars.model.OwnershipHistoryId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PostEditDto {
    private int id;
    private String carName;
    private int categoryId;
    private int typeId;
    private int brandId;
    private String engine;
    private String description;
    private BigDecimal price;
    private BigDecimal beforePrice;
    private boolean sold;
    private List<Integer> removedFileIds = new ArrayList<>();
    private List<MultipartFile> newPhotos = new ArrayList<>();
    private List<OwnershipHistoryDto> ownershipHistories = new ArrayList<>();
    private List<OwnershipHistoryId> removedOwnersIds = new ArrayList<>();
}
