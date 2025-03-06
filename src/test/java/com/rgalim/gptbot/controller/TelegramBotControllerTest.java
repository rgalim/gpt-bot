package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.exception.TelegramApiException;
import com.rgalim.gptbot.model.telegram.ErrorResponse;
import com.rgalim.gptbot.service.TelegramBotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static com.rgalim.gptbot.utils.TelegramUtils.*;
import static com.rgalim.gptbot.utils.TestConstants.*;
import static org.mockito.Mockito.*;

@WebFluxTest(controllers = TelegramBotController.class)
class TelegramBotControllerTest {

    private static final String UPDATE_PATH = "/api/v1/telegram/update";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TelegramBotService telegramBotService;

    @Test
    void whenHandledUpdateThenReturnSuccess() {
        when(telegramBotService.handleUpdate(UPDATE)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(UPDATE)
                .header(BOT_AUTH_HEADER, "abcd")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenFailedToHandleUpdateThenReturnErrorResponse() {
        when(telegramBotService.handleUpdate(UPDATE))
                .thenReturn(Mono.error(new TelegramApiException("Something went wrong")));

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(UPDATE)
                .header(BOT_AUTH_HEADER, "abcd")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .isEqualTo(new ErrorResponse("Something went wrong"));
    }
}