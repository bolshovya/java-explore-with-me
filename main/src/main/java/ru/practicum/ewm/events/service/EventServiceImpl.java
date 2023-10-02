package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.storage.CategoryRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.EventParam;
import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.location.Location;
import ru.practicum.ewm.events.location.dto.LocationDto;
import ru.practicum.ewm.events.location.dto.LocationMapper;
import ru.practicum.ewm.events.location.storage.LocationRepository;
import ru.practicum.ewm.events.storage.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.Request;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.dto.RequestState;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.stats.client.StatClient;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.users.User;
import ru.practicum.ewm.users.storage.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final LocationRepository locationRepository;

    private final UserRepository userRepository;

    private final CategoryRepository categoryRepository;

    private final RequestRepository requestRepository;

    private final StatClient statClient;

    @Override
    @Transactional
    public EventFullDto create(Long userId, NewEventDto newEventDto) {
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
    public EventFullDto updateEventByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
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
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
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

    @Override
    public List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size) {
        log.info("EventServiceImpl: получение всех событий, добавленных пользователем с id: {}", userId);
        Pageable pageable = PageRequest.of(from / size, size);

        return eventRepository.findAllByInitiatorId(userId, pageable)
                .stream().map(EventMapper::getEventShortDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByInitiatorId(Long userId, Long eventId) {
        log.info("EventServiceImpl: получение события с id: {}, добавленное пользователем с id: {}", eventId, userId);

        return EventMapper.getEventFullDto(eventRepository.findByIdAndInitiatorId(eventId, userId));
    }

    @Override
    public List<EventFullDto> getAllByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                            LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("EventServiceImpl: получение списка всех событий с id пользователей: {}, статусом: {}, " +
                "категорией: {}, rangeStart: {}, rangeEnd: {}", users, states, categories, rangeStart, rangeEnd);
        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllByAdmin(users, states, categories, rangeStart, rangeEnd, pageable);

        return events.stream().map(EventMapper::getEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto getByIdPublic(Long id, HttpServletRequest request) {
        log.info("EventServiceImpl: получение события с id: {}", id);

        statClient.createEndpointHit(EndpointHit.builder()
                .app("Explore_with_me")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        return EventMapper.getEventFullDto(eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Event with id=" + id +" was not found")));
    }

    @Override
    public List<EventShortDto> getAllPublic(EventParam eventParam) {
        log.info("EventServiceImpl: публичный запрос на получение списка событий");

        statClient.createEndpointHit(EndpointHit.builder()
                .app("Explore_with_me")
                .uri(eventParam.getRequest().getRequestURI())
                .ip(eventParam.getRequest().getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());

        List<Event> events = eventRepository.findAllPublic(eventParam.getText(), eventParam.getCategories(), eventParam.getPaid(),
                eventParam.getRangeStart(), eventParam.getRangeEnd(), eventParam.getPageable());

        if (eventParam.getOnlyAvailable()) {
            events = events.stream().filter(x -> x.getParticipantLimit().equals(0)).collect(Collectors.toList());
        }

        if (eventParam.getSort().equals(SortState.EVENT_DATE)) {
            events = events.stream().sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
        } else if (eventParam.getSort().equals(SortState.VIEWS)) {
            events = events.stream().sorted(Comparator.comparing(Event::getViews)).collect(Collectors.toList());
        }

        return events.stream().map(EventMapper::getEventShortDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult patchRequestStatus(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("EventServiceImpl: изменение статуса для запросов с id: {}",
                eventRequestStatusUpdateRequest.getRequestIds());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        if (!event.getInitiator().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не инициатор события");
        }

        Long confirmLimit = requestRepository.countByEventIdAndStatus(eventId, RequestState.CONFIRMED);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= confirmLimit) {
            throw new ForbiddenException("The participant limit has been reached");
        }

        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();


        for (Request request : requestsToUpdate) {
            if (!request.getStatus().equals(RequestState.PENDING)) {
                throw new ForbiddenException("Статус заявки должен быть PENDING");
            }

            if (!request.getEvent().getId().equals(eventId)) {
                rejectedRequests.add(request);
                continue;
            }

            if (eventRequestStatusUpdateRequest.getStatus().equals(EventRequestStatusUpdateRequest.StateAction.CONFIRMED)) {
                if (confirmLimit < event.getParticipantLimit()) {
                    request.setStatus(RequestState.CONFIRMED);
                    confirmLimit++;
                    confirmedRequests.add(request);
                } else {
                    request.setStatus(RequestState.REJECTED);
                    rejectedRequests.add(request);
                }
            } else if (eventRequestStatusUpdateRequest.getStatus().equals(EventRequestStatusUpdateRequest.StateAction.REJECTED)) {
                request.setStatus(RequestState.REJECTED);
                rejectedRequests.add(request);
            }

        }

        requestRepository.saveAll(confirmedRequests);

        return EventRequestStatusUpdateResult.builder()
                .rejectedRequests(rejectedRequests.stream().map(RequestMapper::getParticipationRequestDto).collect(Collectors.toList()))
                .confirmedRequests(confirmedRequests.stream().map(RequestMapper::getParticipationRequestDto).collect(Collectors.toList()))
                .build();

    }

    @Override
    public List<ParticipationRequestDto> getRequestByInitiatorId(Long eventId, Long userId) {
        log.info("EventServiceImpl: получение списка запросов для события с id: {}, пользователя с id: {}", eventId, userId);
        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::getParticipationRequestDto).collect(Collectors.toList());
    }
}
