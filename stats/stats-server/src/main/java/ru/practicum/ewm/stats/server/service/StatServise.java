package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.stats.dto.EndpointHit;

public interface StatServise {
    EndpointHit create(EndpointHit endpointHit);
}
