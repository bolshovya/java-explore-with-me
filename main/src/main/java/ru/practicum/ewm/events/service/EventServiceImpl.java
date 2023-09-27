package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.categories.storage.CategoryRepository;
import ru.practicum.ewm.events.Event;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventMapper;
import ru.practicum.ewm.events.dto.EventState;
import ru.practicum.ewm.events.dto.NewEventDto;
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
}
