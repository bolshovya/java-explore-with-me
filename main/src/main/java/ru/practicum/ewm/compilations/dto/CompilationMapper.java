package ru.practicum.ewm.compilations.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.events.dto.EventMapper;

import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {
    public static Compilation getCompilation(NewCompilationDto newCompilationDto) {
        return Compilation.builder()
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.getPinned())
                .build();
    }

    public static CompilationDto getCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .title(compilation.getTitle())
                .pinned(compilation.getPinned())
                .events(compilation.getEvents().stream().map(EventMapper::getEventShortDto).collect(Collectors.toList()))
                .build();
    }
}
