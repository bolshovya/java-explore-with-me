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
import java.util.List;
import java.util.stream.Collectors;

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

        if (eventFromDb.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (eventFromDb.getParticipantLimit() > 0) {
            if (eventFromDb.getParticipantLimit() <= requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED)) {
                throw new ForbiddenException("У события достигнут лимит запросов на участие");
            }
        }
        if (!eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Нельзя участвовать в неопубликованном событии");
        }
        request.setRequester(userFromDb);

        request.setEvent(eventFromDb);

        request.setStatus((eventFromDb.getRequestModeration() && !eventFromDb.getParticipantLimit().equals(0)) ?
                RequestState.PENDING : RequestState.CONFIRMED);

        request = requestRepository.save(request);

        return RequestMapper.getParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> findAllByUserId(Long userId) {
        log.info("RequestServiceImpl: получение списка запросов для пользователя с id: {}", userId);

        return requestRepository.findAllByRequesterId(userId).stream()
                .map(RequestMapper::getParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipationRequestDto cancellingRequest(Long userId, Long requestId) {
        log.info("RequestServiceImpl: пользователь с id: {} отменил запрос с id: {} на участии в событии",
                userId, requestId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException();
        }
        request.setStatus(RequestState.CANCELED);
        request = requestRepository.save(request);

        return RequestMapper.getParticipationRequestDto(request);
    }
}
