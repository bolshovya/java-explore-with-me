package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.model.EventParam;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.events.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

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

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getAllByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @Valid @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @Valid @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        if (users != null && users.size() == 1 && users.get(0).equals(0L)) {
            users = null;
        }
        if (categories != null && categories.size() == 1 && categories.get(0).equals(0L)) {
            categories = null;
        }
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(100);
        }
        log.info("EventAdminController: получение списка всех событий с id пользователей: {}, статусом: {}, " +
                "категорией: {}, rangeStart: {}, rangeEnd: {}", users, states, categories, rangeStart, rangeEnd, from, size);

        return eventService.getAllByAdmin(EventParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build());
    }
}