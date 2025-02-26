package com.rgalim.gptbot.utils;

import com.rgalim.gptbot.exception.TelegramApiException;
import com.rgalim.gptbot.model.telegram.TelegramErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
public final class TelegramUtils {

    private TelegramUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String GET_UPDATES_ENDPOINT = "/getUpdates";
    public static final String OFFSET_PARAM = "offset";
    public static final String TIMEOUT_PARAM = "timeout";

    public static final String GET_UPDATES_ERROR_MESSAGE = "Failed to get bot updates. Error: ";

    public static Mono<TelegramApiException> mapToTelegramErrorResponse(String message, ClientResponse response) {
        return response
                .bodyToMono(TelegramErrorResponse.class)
                .map(errorResponse -> new TelegramApiException(message + errorResponse.description()));
    }

    public static TelegramApiException mapToTelegramApiException(Throwable error) {
        log.error(GET_UPDATES_ERROR_MESSAGE + error.getMessage());
        if (error instanceof TelegramApiException apiException) {
            return apiException;
        }
        return new TelegramApiException(GET_UPDATES_ERROR_MESSAGE + error.getMessage());
    }
}
