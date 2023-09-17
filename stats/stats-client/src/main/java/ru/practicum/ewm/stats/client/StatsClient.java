package ru.practicum.ewm.stats.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StatsClient {

    @Value("${stats.uri}")
    private String uri;

    private final RestTemplate restTemplate;

    public String saveStats(String item) {
        ResponseEntity<String> response = restTemplate.postForEntity(uri + "/stats", item, String.class);
        return response.getBody();
    }

    public Integer getCount() {
        ResponseEntity<Integer> response = restTemplate.getForEntity(uri + "/", Integer.class);
        return response.getBody();
    }
}
