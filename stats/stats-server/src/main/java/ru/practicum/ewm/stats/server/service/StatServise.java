package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStat;

import java.time.LocalDateTime;
import java.util.List;

public interface StatServise {
    EndpointHit create(EndpointHit endpointHit);

    List<ViewStat> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
