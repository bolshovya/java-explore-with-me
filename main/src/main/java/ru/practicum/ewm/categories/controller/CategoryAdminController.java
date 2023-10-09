package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CategoryAdminController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CategoryDto create(
            @Valid @RequestBody NewCategoryDto newCategoryDto
    ) {
        log.info("CategoryAdminController: сохранение категории: {}", newCategoryDto);
        return categoryService.create(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCategoryById(
            @PathVariable Long catId
    ) {
        log.info("CategoryAdminController: удаление категории с id: {}", catId);
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryDto update(
            @PathVariable Long catId,
            @Valid @RequestBody NewCategoryDto newCategoryDto
    ) {
        log.info("CategoryAdminController: обновление данных для категории с id: {}, {}", catId, newCategoryDto);
        return categoryService.update(catId, newCategoryDto);
    }

}
