package ru.practicum.ewm.stats.server.model;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStatDto;

@UtilityClass
public class StatMapper {

    public static StatModel getStatModel(EndpointHit endpointHit) {
        return StatModel.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public static EndpointHit getStatHitDto(StatModel statModel) {
        return EndpointHit.builder()
                .id(statModel.getId())
                .app(statModel.getApp())
                .uri(statModel.getUri())
                .timestamp(statModel.getTimestamp())
                .build();
    }

    public static ViewStatDto getViewStatDto(ViewStat viewStat) {
        return ViewStatDto.builder()
                .app(viewStat.getApp())
                .uri(viewStat.getUri())
                .hits(viewStat.getHits())
                .build();
    }
}
