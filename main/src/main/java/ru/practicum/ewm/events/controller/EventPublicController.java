package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.service.EventService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class EventPublicController {

    private final EventService eventService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getByIdPublic(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        log.info("EventPublicController: получение события с id: {}", id);
        return eventService.getByIdPublic(id, request);
    }
}
