package com.rgalim.gptbot.client;

import com.rgalim.gptbot.config.TelegramProperties;
import com.rgalim.gptbot.exception.TelegramApiException;
import com.rgalim.gptbot.model.telegram.Message;
import com.rgalim.gptbot.model.telegram.SendMessageRequest;
import com.rgalim.gptbot.model.telegram.SendMessageResponse;
import com.rgalim.gptbot.model.telegram.UpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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

        return webClient.get()
                .uri(getTelegramUrl(GET_UPDATES_ENDPOINT), uriBuilder -> uriBuilder
                        .queryParam(OFFSET_PARAM, lastUpdateId.get() + 1)
                        .queryParam(TIMEOUT_PARAM, properties.timeout())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> mapToTelegramErrorResponse(GET_UPDATES_ERROR_MESSAGE, response))
                .bodyToMono(UpdatesResponse.class)
                .doOnSuccess(response -> log.info("Successfully got bot updates: {}", response.result()))
                .doOnError(error -> log.error(error.getMessage()))
                .retryWhen(getRetrySpec("getUpdates"))
                .onErrorResume(error -> Mono.empty());
    }

    public Mono<Message> sendMessage(String text, String chatId) {
        log.info("Sending message to chat {}", chatId);
        SendMessageRequest sendMessageRequest = new SendMessageRequest(chatId, text);

        return webClient.post()
                .uri(getTelegramUrl(SEND_MESSAGE_ENDPOINT))
                .bodyValue(sendMessageRequest)
                .retrieve()
                .onStatus(HttpStatusCode::isError,
                        response -> mapToTelegramErrorResponse(SEND_TEXT_ERROR_MESSAGE, response))
                .bodyToMono(SendMessageResponse.class)
                .map(SendMessageResponse::result)
                .doOnSuccess(message -> log.info("Successfully sent message: {}", message))
                .doOnError(error -> log.error(error.getMessage()))
                .retryWhen(getRetrySpec("sendMessage"))
                .onErrorResume(error -> Mono.empty());
    }

    private Retry getRetrySpec(String message) {
        return Retry.backoff(
                        properties.retryAttempts(),
                        Duration.ofMillis(properties.retryBackoff())
                )
                .filter(TelegramApiException.class::isInstance)
                .doBeforeRetry(retrySignal ->
                        log.warn("Retrying to call {} because of error: {}. Attempt #{}",
                                message, retrySignal.failure().getMessage(), retrySignal.totalRetries()));
    }

    private String getTelegramUrl(String path) {
        return properties.url() + BOT_PATH + properties.botToken() + path;
    }
}
