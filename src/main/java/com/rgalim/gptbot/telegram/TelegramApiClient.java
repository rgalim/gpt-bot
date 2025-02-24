package com.rgalim.gptbot.telegram;

import com.rgalim.gptbot.config.TelegramProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static com.rgalim.gptbot.telegram.TelegramUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramApiClient {

    private final WebClient webClient;
    private final TelegramProperties properties;
    private final AtomicLong lastUpdateId = new AtomicLong(0);

    public Flux<Update> getUpdates() {
        return Flux.interval(Duration.ofSeconds(1)) // Retry every second if needed
                .flatMap(ignore -> fetchBotUpdates());
    }

    private Mono<Update> fetchBotUpdates() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(GET_UPDATES_ENDPOINT)
                        .queryParam(OFFSET_PARAM, lastUpdateId.get() + 1)
                        .queryParam(TIMEOUT_PARAM, properties.timeout())
                        .build(properties.token()))
                .retrieve()
                .bodyToMono(Update.class);
    }
}
