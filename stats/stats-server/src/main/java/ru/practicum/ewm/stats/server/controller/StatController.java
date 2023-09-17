package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStat;
import ru.practicum.ewm.stats.server.service.StatServise;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class StatController {

    private final StatServise statServise;

    @PostMapping("/hit")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EndpointHit create(
            @RequestBody EndpointHit endpointHit
    ) {
        log.info("StatController POST: сохранение статистики: {}", endpointHit);
        return statServise.create(endpointHit);
    }

    @GetMapping("/stats")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ViewStat> getStats(
            @RequestParam(value = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(value = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(value = "uris") List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") Boolean unique
    ) {
        log.info("StatController GET: получение статистик uri: {}", uris);
        return statServise.getStats(start, end, uris, unique);

    }

}
