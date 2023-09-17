package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStat;
import ru.practicum.ewm.stats.server.model.StatMapper;
import ru.practicum.ewm.stats.server.model.StatModel;
import ru.practicum.ewm.stats.server.storage.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatServiceImpl implements StatServise {

    private final StatRepository statRepository;

    @Override
    public EndpointHit create(EndpointHit endpointHit) {
        log.info("StatServiceImpl: сохранение статистики: {}", endpointHit);
        StatModel statToDb = StatMapper.getStatModel(endpointHit);
        StatModel statFromDb = statRepository.save(statToDb);
        log.info("StatServiceImpl: статистике присвоен id: {}", statFromDb.getId());
        return StatMapper.getStatHitDto(statFromDb);
    }

    public ViewStat getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        log.info("StatServiceImpl GET: получение статистик uri: {}", uris);

    }
}
