package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

@RestController
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class RequestPrivateController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto create(
            @PathVariable Long userId,
            @RequestParam Long eventId
    ) {
        log.info("RequestPrivateController: сохранение запроса на событие с id: {}, для пользователя с id: {}",
                eventId, userId);

        return requestService.create(userId, eventId);
    }
}
