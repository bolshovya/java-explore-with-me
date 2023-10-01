package ru.practicum.ewm.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

public interface RequestService {
    @Transactional
    ParticipationRequestDto create(Long userId, Long eventId);
}
