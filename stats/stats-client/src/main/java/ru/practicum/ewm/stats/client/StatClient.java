package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StatClient {

    private final WebClient webClient;

    @Autowired
    public StatClient(@Value("${ewm-server.url}") String serverUrl) {
        webClient = WebClient.builder().baseUrl(serverUrl).build();
    }


    public Mono<EndpointHit> createEndpointHit(EndpointHit endpointHit) {

        return webClient.post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(endpointHit), EndpointHit.class)
                .retrieve()
                .bodyToMono(EndpointHit.class);

    }

    public Mono<List<ViewStatDto>> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        return webClient.get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}", start, end, uris, unique)
                .retrieve()
                .bodyToFlux(ViewStatDto.class)
                .collectList();
    }

}
