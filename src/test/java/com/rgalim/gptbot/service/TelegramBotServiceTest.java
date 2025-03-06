package com.rgalim.gptbot.service;

import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.*;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static com.rgalim.gptbot.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramBotServiceTest {

    @Mock
    private TelegramApiClient telegramApiClient;

    @Mock
    private TelegramCommandService telegramCommandService;

    @InjectMocks
    private TelegramBotService telegramBotService;

    @Nested
    class GetUpdates {

        @Test
        void whenSuccessfulResponseFromTelegramApiThenReturnUpdatesResponse() {
            Message message1 = new Message(1, USER, 12345, "Message text1");
            List<Update> updates1 = List.of(new Update(1, message1));
            UpdatesResponse updatesResponse1 = new UpdatesResponse(true, updates1);

            Message message2 = new Message(2, USER, 45678, "Message text2");
            List<Update> updates2 = List.of(new Update(2, message2));
            UpdatesResponse updatesResponse2 = new UpdatesResponse(true, updates2);

            List<Long> capturedValues = new ArrayList<>();

            when(telegramApiClient.fetchBotUpdates(any(AtomicLong.class))).thenAnswer(invocation -> {
                AtomicLong argument = invocation.getArgument(0);
                capturedValues.add(argument.get());
                return capturedValues.size() == 1 ? Mono.just(updatesResponse1) : Mono.just(updatesResponse2);
            });

            StepVerifier.create(telegramBotService.getUpdates().take(2))
                    .expectNext(updatesResponse1)
                    .expectNext(updatesResponse2)
                    .verifyComplete();

            verify(telegramApiClient, times(2)).fetchBotUpdates(any(AtomicLong.class));
            assertEquals(2, capturedValues.size());
            assertEquals(0, capturedValues.get(0));
            assertEquals(1, capturedValues.get(1));
        }

        @Test
        void whenFailedResponseFromTelegramApiThenRepeat() {
            Message message1 = new Message(1, USER, 12345, "Message text1");
            List<Update> updates1 = List.of(new Update(1, message1));
            UpdatesResponse updatesResponse1 = new UpdatesResponse(true, updates1);

            Message message2 = new Message(2, USER, 45678, "Message text2");
            List<Update> updates2 = List.of(new Update(2, message2));
            UpdatesResponse updatesResponse2 = new UpdatesResponse(true, updates2);

            List<Long> capturedValues = new ArrayList<>();

            when(telegramApiClient.fetchBotUpdates(any(AtomicLong.class))).thenAnswer(invocation -> {
                AtomicLong argument = invocation.getArgument(0);
                capturedValues.add(argument.get());
                if (capturedValues.size() == 1) {
                    return Mono.just(updatesResponse1);
                } else if (capturedValues.size() == 2) {
                    return Mono.empty();
                }
                return Mono.just(updatesResponse2);
            });

            StepVerifier.create(telegramBotService.getUpdates().take(2))
                    .expectNext(updatesResponse1)
                    .expectNext(updatesResponse2)
                    .verifyComplete();

            verify(telegramApiClient, times(3)).fetchBotUpdates(any(AtomicLong.class));
            assertEquals(3, capturedValues.size());
            assertEquals(0, capturedValues.get(0));
            assertEquals(1, capturedValues.get(1));
            assertEquals(1, capturedValues.get(2));
        }
    }

    @Nested
    class HandleUpdate {

        @Test
        void whenTextMessageInUpdateThenHandle() {
            when(telegramCommandService.isCommand("Message text")).thenReturn(false);

            StepVerifier.create(telegramBotService.handleUpdate(UPDATE))
                    .verifyComplete();

            verify(telegramCommandService, never()).handleCommand(any());
        }

        @Test
        void whenCommandInUpdateThenHandle() {
            Message message = new Message(1, USER, 12345, "/start");
            Update update = new Update(1, message);

            Command command = new Command(CommandType.START, "1", "/start");

            when(telegramCommandService.isCommand("/start")).thenReturn(true);
            when(telegramCommandService.handleCommand(command)).thenReturn(Mono.empty());

            StepVerifier.create(telegramBotService.handleUpdate(update))
                    .verifyComplete();
        }

        @Test
        void whenUnsupportedCommandInUpdateThenDoNothing() {
            Message message = new Message(1, USER, 12345, "/unknown");
            Update update = new Update(1, message);

            when(telegramCommandService.isCommand("/unknown")).thenReturn(true);

            StepVerifier.create(telegramBotService.handleUpdate(update))
                    .verifyComplete();

            verify(telegramCommandService, never()).handleCommand(any());
        }
    }
}