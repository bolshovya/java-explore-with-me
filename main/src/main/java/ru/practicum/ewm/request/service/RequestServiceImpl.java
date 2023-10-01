package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.storage.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.RequestState;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.storage.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Override
    @Transactional
    public ParticipationRequestDto create(Long userId, Long eventId) {
        log.info("RequestServiceImpl: сохранение запроса на событие с id: {}, для пользователя с id: {}",
                eventId, userId);

        if (requestRepository.countByEventIdAndRequesterId(eventId, userId) > 0) {
            throw new ForbiddenException("нельзя добавить повторный запрос");
        }

        Request request = Request.builder().created(LocalDateTime.now()).status(RequestState.PENDING).build();

        Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId +" was not found"));
        User userFromDb = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (eventFromDb.getInitiator().equals(userId)) {
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        request.setRequester(userFromDb);

        if (!eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя участвовать в неопубликованном событии");
        }
        request.setEvent(eventFromDb);

        if (eventFromDb.getRequestModeration().equals(false)) {
            request.setStatus(RequestState.CONFIRMED);
        }

        request = requestRepository.save(request);

        return RequestMapper.getParticipationRequestDto(request);

    }
}
