package com.rgalim.gptbot.service;

import com.openai.errors.OpenAIException;
import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.exception.TelegramApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.rgalim.gptbot.utils.TestConstants.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramMessageServiceTest {

    @Mock
    private OpenAiService openAiService;

    @Mock
    private TelegramApiClient telegramApiClient;

    @InjectMocks
    private TelegramMessageService telegramMessageService;

    @Test
    void whenMessageIsHandledSuccessfullyThenReturnVoidMono() {
        when(openAiService.sendPrompt("Message text")).thenReturn(Mono.just("response"));
        when(telegramApiClient.sendMessage("response", "1")).thenReturn(Mono.just(MESSAGE));

        StepVerifier.create(telegramMessageService.handleMessage(MESSAGE))
                .verifyComplete();
    }

    @Test
    void whenFailedToSendPromptThenReturnErrorMono() {
        when(openAiService.sendPrompt("Message text"))
                .thenReturn(Mono.error(new OpenAIException("Something went wrong")));

        StepVerifier.create(telegramMessageService.handleMessage(MESSAGE))
                .expectErrorMatches(error -> error instanceof OpenAIException &&
                        error.getMessage().equals("Something went wrong"))
                .verify();

        verifyNoInteractions(telegramApiClient);
    }

    @Test
    void whenFailedToSendTelegramMessageThenReturnErrorMono() {
        when(openAiService.sendPrompt("Message text")).thenReturn(Mono.just("response"));
        when(telegramApiClient.sendMessage("response", "1"))
                .thenReturn(Mono.error(new TelegramApiException("Something went wrong")));

        StepVerifier.create(telegramMessageService.handleMessage(MESSAGE))
                .expectErrorMatches(error -> error instanceof TelegramApiException &&
                        error.getMessage().equals("Something went wrong"))
                .verify();
    }
}