package ru.practicum.ewm.categories.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.categories.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Page<Category> findAll(Pageable pageable);

    boolean existsCategoryByName(String name);

    boolean existsCategoryByNameAndIdNot(String name, Long id);
}
