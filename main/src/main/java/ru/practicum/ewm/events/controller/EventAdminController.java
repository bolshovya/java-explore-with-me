package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.dto.UpdateEventUserRequest;
import ru.practicum.ewm.events.service.EventService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventAdminController {

    private final EventService eventService;

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByAdmin(
            @PathVariable Long eventId,
            @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest
    ) {
        log.info("EventAdminController: обновление данных события с id: {}", eventId);
        return eventService.updateEventByAdmin(eventId, updateEventAdminRequest);
    }
}