package ru.practicum.ewm.compilations.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    @Transactional
    CompilationDto create(NewCompilationDto newCompilationDto);

    @Transactional
    void delete(Long compId);

    @Transactional
    CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest);

    List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);
}
