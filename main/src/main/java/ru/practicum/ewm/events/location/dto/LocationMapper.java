package ru.practicum.ewm.events.location.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.events.location.Location;

@UtilityClass
public class LocationMapper {

    public static Location getLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public static LocationDto getLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
