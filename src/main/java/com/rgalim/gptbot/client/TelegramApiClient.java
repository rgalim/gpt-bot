package com.rgalim.gptbot.client;

import com.rgalim.gptbot.config.TelegramProperties;
import com.rgalim.gptbot.model.telegram.UpdatesResponse;
import com.rgalim.gptbot.utils.TelegramUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

import static com.rgalim.gptbot.utils.TelegramUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramApiClient {

    private final WebClient webClient;
    private final TelegramProperties properties;

    public Mono<UpdatesResponse> fetchBotUpdates(AtomicLong lastUpdateId) {
        log.info("Fetching updates from offset: {}", lastUpdateId);
        String url = properties.url() + BOT_PATH + properties.token() + GET_UPDATES_ENDPOINT;

        return webClient.get()
                .uri(url, uriBuilder -> uriBuilder
                        .queryParam(OFFSET_PARAM, lastUpdateId.get() + 1)
                        .queryParam(TIMEOUT_PARAM, properties.timeout())
                        .build(properties.token()))
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> mapToTelegramErrorResponse(GET_UPDATES_ERROR_MESSAGE, response))
                .bodyToMono(UpdatesResponse.class)
                .doOnSuccess(response ->  log.info("Successfully got bot updates: {}", response.result()))
                .onErrorMap(TelegramUtils::mapToTelegramApiException);
    }
}
