package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.storage.CategoryRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.location.Location;
import ru.practicum.ewm.events.location.dto.LocationDto;
import ru.practicum.ewm.events.location.dto.LocationMapper;
import ru.practicum.ewm.events.location.storage.LocationRepository;
import ru.practicum.ewm.events.storage.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.storage.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public EventFullDto create(
            Long userId,
            NewEventDto newEventDto
    ) {
        if(newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + newEventDto.getEventDate());
        }

        log.info("EventServiceImpl: сохранение события: {} для пользователя: {}", newEventDto, userId);
        Event eventToDb = EventMapper.getEvent(newEventDto);

        eventToDb.setCreatedOn(LocalDateTime.now());

        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        eventToDb.setInitiator(initiator);

        eventToDb.setState(EventState.PENDING);

        Long categoryId = newEventDto.getCategory();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));

        eventToDb.setCategory(category);

        LocationDto locationDto = newEventDto.getLocation();
        Location locationFromDb = locationRepository.save(LocationMapper.getLocation(locationDto));

        eventToDb.setLocation(locationFromDb);
        eventToDb.setConfirmedRequests(0);
        eventToDb.setViews(0);

        Event eventFromDb = eventRepository.save(eventToDb);

        return EventMapper.getEventFullDto(eventFromDb);
    }

    /**
     *  изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
     *  дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
     */
    @Override
    @Transactional
    public EventFullDto updateEventByCurrentUser(
            Long userId,
            Long eventId,
            UpdateEventUserRequest updateEventUserRequest
    ) {
        if(updateEventUserRequest.getEventDate() != null &&
                updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, которая еще не наступила. " +
                    "Value: " + updateEventUserRequest.getEventDate());
        }

        Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if(eventFromDb.getState().equals(EventState.PUBLISHED)) {
            throw new ForbiddenException("Only pending or canceled events can be changed");
        }

        log.info("EventServiceImpl: изменение данных события с id: {}, пользователем с id: {}", eventId, userId);

        if (updateEventUserRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategory() != null) {
            Category categoryUpdateEventUserRequest = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() ->
                            new NotFoundException("Category with id=" + updateEventUserRequest.getCategory() + " was not found"));
            eventFromDb.setCategory(categoryUpdateEventUserRequest);
        }

        if (updateEventUserRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            eventFromDb.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            eventFromDb.setLocation(LocationMapper.getLocation(updateEventUserRequest.getLocation()));
        }

        if (updateEventUserRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateAction.SEND_TO_REVIEW)) {
                eventFromDb.setState(EventState.PENDING);
            }
            if (updateEventUserRequest.getStateAction().equals(StateAction.CANCEL_REVIEW)) {
                eventFromDb.setState(EventState.CANCELED);
            }
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventUserRequest.getTitle());
        }

        eventFromDb = eventRepository.save(eventFromDb);
        return EventMapper.getEventFullDto(eventFromDb);
    }
}
