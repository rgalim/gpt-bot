package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.exception.TelegramApiException;
import com.rgalim.gptbot.exception.TokenValidationException;
import com.rgalim.gptbot.model.telegram.ErrorResponse;
import com.rgalim.gptbot.service.AuthService;
import com.rgalim.gptbot.service.TelegramBotService;
import com.rgalim.gptbot.service.TokenValidator;
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
    private TokenValidator tokenValidator;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private TelegramBotService telegramBotService;

    @Test
    void whenHandledUpdateThenReturnSuccess() {
        when(tokenValidator.validateToken(SECRET_TOKEN)).thenReturn(Mono.just(SECRET_TOKEN));
        when(authService.isValidUser("1")).thenReturn(Mono.just(true));
        when(telegramBotService.handleUpdate(UPDATE)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(UPDATE)
                .header(BOT_AUTH_HEADER, SECRET_TOKEN)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void whenFailedToHandleUpdateThenReturnErrorResponse() {
        when(tokenValidator.validateToken(SECRET_TOKEN)).thenReturn(Mono.just(SECRET_TOKEN));
        when(authService.isValidUser("1")).thenReturn(Mono.just(true));
        when(telegramBotService.handleUpdate(UPDATE))
                .thenReturn(Mono.error(new TelegramApiException("Something went wrong")));

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(UPDATE)
                .header(BOT_AUTH_HEADER, SECRET_TOKEN)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .isEqualTo(new ErrorResponse("Something went wrong"));
    }

    @Test
    void whenInvalidTokenThenReturnErrorResponse() {
        TokenValidationException exception = new TokenValidationException("Secret token is invalid");
        when(tokenValidator.validateToken(SECRET_TOKEN)).thenReturn(Mono.error(exception));

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(UPDATE)
                .header(BOT_AUTH_HEADER, SECRET_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .isEqualTo(new ErrorResponse("Request is unauthorized"));

        verifyNoInteractions(telegramBotService);
        verifyNoInteractions(authService);
    }

    @Test
    void whenInvalidUserThenReturnErrorResponse() {
        when(tokenValidator.validateToken(SECRET_TOKEN)).thenReturn(Mono.just(SECRET_TOKEN));
        when(authService.isValidUser("1")).thenReturn(Mono.just(false));

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(UPDATE)
                .header(BOT_AUTH_HEADER, SECRET_TOKEN)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody(ErrorResponse.class)
                .isEqualTo(new ErrorResponse("User is unauthorized"));

        verifyNoInteractions(telegramBotService);
    }
}