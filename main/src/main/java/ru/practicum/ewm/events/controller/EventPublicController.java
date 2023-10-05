package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.EventParam;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.SortState;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.exception.BadRequestException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllPublic(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "VIEWS") SortState sort,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            HttpServletRequest request
    ) {
        log.info("EventPublicController: публичный запрос на получение списка событий");

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusDays(100);
        }
        if (rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException();
        }

        EventParam eventParam = EventParam.builder()
                .request(request)
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        log.info("EventPublicController: {}", eventParam);

        return eventService.getAllPublic(eventParam);
    }
}
