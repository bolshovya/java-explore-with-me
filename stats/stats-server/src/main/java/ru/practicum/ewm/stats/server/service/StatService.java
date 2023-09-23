package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {
    EndpointHit create(EndpointHit endpointHit);

    List<ViewStatDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
