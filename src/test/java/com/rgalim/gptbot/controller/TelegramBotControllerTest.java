package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.model.telegram.ErrorResponse;
import com.rgalim.gptbot.model.telegram.Message;
import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.model.telegram.User;
import com.rgalim.gptbot.service.TelegramBotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.rgalim.gptbot.utils.TelegramUtils.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = TelegramBotController.class)
class TelegramBotControllerTest {

    private static final String UPDATE_PATH = "/api/v1/telegram/update";

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private TelegramBotService telegramBotService;

    @Test
    void whenHandledUpdateThenReturnSuccess() {
        Message message = new Message(
                1,
                new User(1, false, "FirstName", "LastName", "username"),
                12345,
                "Message text1");
        Update update = new Update(1, message);

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(update)
                .header(BOT_AUTH_HEADER, "abcd")
                .exchange()
                .expectStatus().isOk();

        verify(telegramBotService).handleUpdate(update);
    }

    @Test
    void whenFailedToHandleUpdateThenReturnErrorResponse() {
        Message message = new Message(
                1,
                new User(1, false, "FirstName", "LastName", "username"),
                12345,
                "Message text1");
        Update update = new Update(1, message);

        doThrow(new RuntimeException("Error")).when(telegramBotService).handleUpdate(update);

        webTestClient.post()
                .uri(UPDATE_PATH)
                .bodyValue(update)
                .header(BOT_AUTH_HEADER, "abcd")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponse.class)
                .isEqualTo(new ErrorResponse("Something went wrong"));
    }
}