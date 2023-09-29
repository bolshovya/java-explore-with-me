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

        updateEvent(updateEventUserRequest, eventFromDb);

        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction().equals(StateActionUserRequest.SEND_TO_REVIEW)) {
                eventFromDb.setState(EventState.PENDING);
            }
            if (updateEventUserRequest.getStateAction().equals(StateActionUserRequest.CANCEL_REVIEW)) {
                eventFromDb.setState(EventState.CANCELED);
            }
        }

        eventFromDb = eventRepository.save(eventFromDb);
        return EventMapper.getEventFullDto(eventFromDb);
    }

    private void updateEvent(Updatable updateEventRequest, Event eventFromDb) {
        if (updateEventRequest.getAnnotation() != null) {
            eventFromDb.setAnnotation(updateEventRequest.getAnnotation());
        }

        if (updateEventRequest.getCategory() != null) {
            Category categoryUpdateEventUserRequest = categoryRepository.findById(updateEventRequest.getCategory())
                    .orElseThrow(() ->
                            new NotFoundException("Category with id=" + updateEventRequest.getCategory() + " was not found"));
            eventFromDb.setCategory(categoryUpdateEventUserRequest);
        }

        if (updateEventRequest.getDescription() != null) {
            eventFromDb.setDescription(updateEventRequest.getDescription());
        }

        if (updateEventRequest.getEventDate() != null) {
            eventFromDb.setEventDate(updateEventRequest.getEventDate());
        }

        if (updateEventRequest.getLocation() != null) {
            eventFromDb.setLocation(LocationMapper.getLocation(updateEventRequest.getLocation()));
        }

        if (updateEventRequest.getPaid() != null) {
            eventFromDb.setPaid(updateEventRequest.getPaid());
        }

        if (updateEventRequest.getParticipantLimit() != null) {
            eventFromDb.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }

        if (updateEventRequest.getRequestModeration() != null) {
            eventFromDb.setRequestModeration(updateEventRequest.getRequestModeration());
        }

        if (updateEventRequest.getTitle() != null) {
            eventFromDb.setTitle(updateEventRequest.getTitle());
        }
    }

    /**
     * дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
     * событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
     * событие можно отклонить, только если оно еще не опубликовано (Ожидается код ошибки 409)
     */
    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(
            Long eventId,
            UpdateEventAdminRequest updateEventAdminRequest
    ) {
        if(updateEventAdminRequest.getEventDate() != null &&
                updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new ForbiddenException("Field: eventDate. Error: должно быть не ранее, чем за час от даты публикации " +
                    "Value: " + updateEventAdminRequest.getEventDate());
        }

        log.info("EventServiceImpl: изменение данных события с id: {}", eventId);

        Event eventFromDb = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        log.info("EventServiceImpl: события с id: {} найдено в БД: {}", eventId, eventFromDb);

        if(updateEventAdminRequest.getStateAction() != null) {
            if(updateEventAdminRequest.getStateAction().equals(StateActionAdminRequest.PUBLISH_EVENT) &&
                    !eventFromDb.getState().equals(EventState.PENDING)) {
                throw new ForbiddenException("Cannot publish the event because it's not in the right state: " + eventFromDb.getState());
            }

            if(updateEventAdminRequest.getStateAction().equals(StateActionAdminRequest.REJECT_EVENT) &&
                    eventFromDb.getState().equals(EventState.PUBLISHED)) {
                throw new ForbiddenException("Cannot reject the event because it's not in the right state: " + eventFromDb.getState());
            }
        }

        updateEvent(updateEventAdminRequest, eventFromDb);

        log.info("EventServiceImpl: данные события обновлены: {}", eventFromDb);

        if (updateEventAdminRequest.getStateAction() != null) {
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdminRequest.PUBLISH_EVENT)) {
                eventFromDb.setState(EventState.PUBLISHED);
                eventFromDb.setPublishedOn(LocalDateTime.now());
            }
            if (updateEventAdminRequest.getStateAction().equals(StateActionAdminRequest.REJECT_EVENT)) {
                eventFromDb.setState(EventState.CANCELED);
            }
        }

        eventFromDb = eventRepository.save(eventFromDb);
        log.info("EventServiceImpl: новые данные события сохранены в БД: {}", eventFromDb);

        locationRepository.save(eventFromDb.getLocation());
        return EventMapper.getEventFullDto(eventFromDb);
    }
}
