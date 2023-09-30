package ru.practicum.ewm.events.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.*;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    @Transactional
    EventFullDto create(Long userId, NewEventDto newEventDto);

    @Transactional
    EventFullDto updateEventByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    @Transactional
    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto getByInitiatorId(Long userId, Long eventId);

    List<EventFullDto> getAllByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
