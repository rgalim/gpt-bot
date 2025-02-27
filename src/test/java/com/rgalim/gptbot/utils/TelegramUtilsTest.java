package com.rgalim.gptbot.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgalim.gptbot.exception.TelegramApiException;
import com.rgalim.gptbot.model.telegram.TelegramErrorResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.test.StepVerifier;

import static com.rgalim.gptbot.utils.TelegramUtils.*;
import static org.junit.jupiter.api.Assertions.*;

class TelegramUtilsTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String ERROR_MESSAGE = "Error message: ";

    @Nested
    class MapToTelegramErrorResponse {

        @Test
        void whenValidErrorResponseThenMapToTelegramErrorResponse() throws JsonProcessingException {
            TelegramErrorResponse telegramErrorResponse = new TelegramErrorResponse(false, 404, "Not found");
            ClientResponse clientResponse = ClientResponse
                    .create(HttpStatusCode.valueOf(404))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(OBJECT_MAPPER.writeValueAsString(telegramErrorResponse))
                    .build();

            StepVerifier.create(mapToTelegramErrorResponse(ERROR_MESSAGE, clientResponse))
                    .expectNextMatches(apiException ->
                            apiException.getMessage().equals("Error message: Not found"))
                    .verifyComplete();
        }

        @Test
        void whenInvalidErrorResponseThenMapToMonoError() {
            ClientResponse clientResponse = ClientResponse
                    .create(HttpStatusCode.valueOf(404))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body("Something went wrong")
                    .build();

            StepVerifier.create(mapToTelegramErrorResponse(ERROR_MESSAGE, clientResponse))
                    .expectError(DecodingException.class)
                    .verify();
        }
    }

    @Nested
    class MapToTelegramApiException {

        @Test
        void whenTelegramApiExceptionThenReturnThisException() {
            TelegramApiException telegramApiException = new TelegramApiException(ERROR_MESSAGE);

            TelegramApiException actual = mapToTelegramApiException(telegramApiException);

            assertEquals(telegramApiException, actual);
        }

        @Test
        void whenUnexpectedExceptionThenMapToTelegramApiException() {
            RuntimeException unexpectedException = new RuntimeException("Something went wrong");

            TelegramApiException actual = mapToTelegramApiException(unexpectedException);

            assertEquals("Failed to get bot updates. Error: Something went wrong", actual.getMessage());
        }
    }
}