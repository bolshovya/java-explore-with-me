package ru.practicum.ewm.events.service;

import ru.practicum.ewm.events.dto.*;
import ru.practicum.ewm.events.model.EventParam;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {

    EventFullDto create(Long userId, NewEventDto newEventDto);

    EventFullDto updateEventByCurrentUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllByInitiatorId(Long userId, Integer from, Integer size);

    EventFullDto getByInitiatorId(Long userId, Long eventId);

    List<EventFullDto> getAllByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                     LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    List<EventFullDto> getAllByAdmin(EventParam eventParam);

    EventFullDto getByIdPublic(Long id, HttpServletRequest request);

    List<EventShortDto> getAllPublic(EventParam eventParam);

    EventRequestStatusUpdateResult patchRequestStatus(Long userId, Long eventId,
                                                      EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getRequestByInitiatorId(Long eventId, Long userId);
}
