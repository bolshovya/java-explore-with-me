package ru.practicum.ewm.categories.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.categories.model.Category;

@UtilityClass
public class CategoryMapper {
    public static Category getCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto getCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
