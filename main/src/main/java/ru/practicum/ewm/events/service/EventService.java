package ru.practicum.ewm.events.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;

public interface EventService {
    @Transactional
    EventFullDto create(Long userId, NewEventDto newEventDto);
}
