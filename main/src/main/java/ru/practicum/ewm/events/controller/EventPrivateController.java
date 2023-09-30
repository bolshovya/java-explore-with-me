package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventPrivateController {

    private final EventService eventService;

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(
            @PathVariable Long userId,
            @Valid @RequestBody NewEventDto newEventDto
    ) {
        log.info("EventPrivateController: сохранение события: {} для пользователя: {}", newEventDto, userId);
        return eventService.create(userId, newEventDto);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByCurrentUser(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest
    ) {
        log.info("EventPrivateController: изменение данных события с id: {}, пользователем с id: {}", eventId, userId);
        return eventService.updateEventByCurrentUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllByInitiatorId(
            @PathVariable Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @PositiveOrZero Integer size
    ) {
        log.info("EventPrivateController: получение всех событий, добавленных пользователем с id: {}", userId);
        return eventService.getAllByInitiatorId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getByInitiatorId(
            @PathVariable Long userId,
            @PathVariable Long eventId
    ) {
        log.info("EventPrivateController: получение события с id: {}, добавленное пользователем с id: {}", eventId, userId);
        return eventService.getByInitiatorId(userId, eventId);
    }
}