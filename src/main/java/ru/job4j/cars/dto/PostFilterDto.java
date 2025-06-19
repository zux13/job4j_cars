package ru.job4j.cars.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostFilterDto {
    private List<Integer> brandIds;
    private boolean onlyWithPhoto;
    private boolean onlyMyPosts;
    private boolean onlyActive;
    private int userId;
    private int periodId;
    private int limit;
    private int offset;
    private int size;

    public boolean isEmpty() {
        return periodId == 0
                && !onlyWithPhoto
                && !onlyMyPosts
                & !onlyActive
                && (brandIds == null || brandIds.isEmpty());
    }
}
