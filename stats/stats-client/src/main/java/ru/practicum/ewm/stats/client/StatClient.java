package ru.practicum.ewm.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStatDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class StatClient {

    private final WebClient webClient;

    @Autowired
    public StatClient(@Value("${ewm_server_url}") String serverUrl) {
        webClient = WebClient.builder().baseUrl(serverUrl).build();
    }


    public ClientResponse createEndpointHit(EndpointHit endpointHit) {

        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(endpointHit))
                .exchange().block();
    }

    public List<ViewStatDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        return webClient.get()
                .uri("/stats?start={start}&end={end}&uris={uris}&unique={unique}",
                        start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        String.join(",", uris),
                        unique)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToFlux(ViewStatDto.class)
                .collectList().block();
    }
}
