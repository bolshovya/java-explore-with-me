package ru.practicum.ewm.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    @Transactional
    ParticipationRequestDto create(Long userId, Long eventId);

    List<ParticipationRequestDto> findAllByUserId(Long userId);

    ParticipationRequestDto cancellingRequest(Long userId, Long requestId);
}
