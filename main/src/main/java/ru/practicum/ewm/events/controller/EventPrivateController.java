package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.service.EventService;

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
            @RequestBody NewEventDto newEventDto
    ) {
        log.info("EventPrivateController: сохранение события: {} для пользователя: {}", newEventDto, userId);
        return eventService.create(userId, newEventDto);
    }
}
