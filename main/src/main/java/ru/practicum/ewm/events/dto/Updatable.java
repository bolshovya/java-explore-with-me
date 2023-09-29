package ru.practicum.ewm.events.dto;

import ru.practicum.ewm.categories.Category;
import ru.practicum.ewm.events.location.dto.LocationDto;

import java.time.LocalDateTime;

public interface Updatable {

    String getAnnotation();

    Long getCategory();

    String getDescription();

    LocalDateTime getEventDate();

    LocationDto getLocation();

    Boolean getPaid();

    Integer getParticipantLimit();

    Boolean getRequestModeration();

    String getTitle();

}
