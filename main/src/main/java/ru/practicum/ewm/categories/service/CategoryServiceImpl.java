package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.CategoryMapper;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.storage.CategoryRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        log.info("CategoryServiceImpl: сохранение категории: {}", newCategoryDto);
        if (categoryRepository.existsCategoryByName(newCategoryDto.getName())) {
            throw new ForbiddenException();
        }
        Category categoryFromDb = categoryRepository.save(CategoryMapper.getCategory(newCategoryDto));
        log.info("CategoryServiceImpl: категории присвоен id: {}", categoryFromDb.getId());
        return CategoryMapper.getCategoryDto(categoryFromDb);
    }

    @Override
    @Transactional
    public void deleteCategoryById(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));
        log.info("CategoryServiceImpl: удаление категории с id: {}", catId);
        categoryRepository.deleteById(catId);
        log.info("CategoryServiceImpl: категория с id: {} удалена", catId);
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, NewCategoryDto newCategoryDto) {
        Category categoryFromDb = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));

        if (categoryRepository.existsCategoryByNameAndIdNot(newCategoryDto.getName(),catId)) {
            throw new ForbiddenException();
        }
        log.info("CategoryServiceImpl: обновление данных для категории с id: {}, {}", catId, newCategoryDto);
        categoryFromDb.setName(newCategoryDto.getName());

        return CategoryMapper.getCategoryDto(categoryRepository.save(categoryFromDb));
    }

    @Override
    public List<CategoryDto> findAll(Integer from, Integer size) {
        log.info("CategoryServiceImpl: получение списка всех категорий");
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categoriesFromDb = categoryRepository.findAll(pageable).getContent();
        log.info("CategoryServiceImpl: получен список из {} позиций", categoriesFromDb.size());
        return categoriesFromDb.stream().map(CategoryMapper::getCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long catId) {
        log.info("CategoryServiceImpl: получение категории с id: {}", catId);

        Category categoryFromDb = categoryRepository.findById(catId)
                .orElseThrow(() ->
                        new NotFoundException("Category with id=" + catId + " was not found"));
        return CategoryMapper.getCategoryDto(categoryFromDb);
    }
}
