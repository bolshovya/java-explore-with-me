package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.CompilationMapper;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilations.storage.CompilationRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        log.info("CompilationServiceImpl: сохранение подборки: {}", newCompilationDto);

        Set<Event> events = newCompilationDto.getEvents().isEmpty() ?
                new HashSet<>() : eventRepository.findAllById(newCompilationDto.getEvents()).stream().collect(Collectors.toSet());

        Compilation compilation = CompilationMapper.getCompilation(newCompilationDto);
        compilation.setEvents(events);

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.getCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        log.info("CompilationServiceImpl: обновление данных для подборки с id: {}", compId);

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.getEvents() != null) {
            compilation.setEvents(new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents())));
        }

        compilation = compilationRepository.save(compilation);

        return CompilationMapper.getCompilationDto(compilation);
    }

    @Override
    @Transactional
    public List<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        log.info("CompilationServiceImpl: получение всех подборок событий");

        Pageable pageable = PageRequest.of(from / size, size);

        return compilationRepository.findAllByPinned(pinned, pageable)
                .stream().map(CompilationMapper::getCompilationDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto getById(Long compId) {
        log.info("CompilationServiceImpl: получение подборки с id: {}", compId);

        return CompilationMapper.getCompilationDto(compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found")));
    }

}
