package ru.practicum.ewm.categories.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    @Transactional
    CategoryDto create(NewCategoryDto newCategoryDto);

    @Transactional
    void deleteCategoryById(Long catId);

    @Transactional
    CategoryDto update(Long catId, NewCategoryDto newCategoryDto);

    List<CategoryDto> findAll(Integer from, Integer size);

    CategoryDto findById(Long catId);
}
