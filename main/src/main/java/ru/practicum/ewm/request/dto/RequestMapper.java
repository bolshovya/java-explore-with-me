package ru.practicum.ewm.request.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.request.Request;

@UtilityClass
public class RequestMapper {

    public static ParticipationRequestDto getParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .created(request.getCreated())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .status(request.getStatus())
                .build();
    }
}
