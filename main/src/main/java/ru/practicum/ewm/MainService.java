package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.client.StatsClient;

@RequiredArgsConstructor
@Service
public class MainService {

    private final StatsClient statsClient;
}
