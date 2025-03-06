package com.rgalim.gptbot.service;

import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.Command;
import com.rgalim.gptbot.model.telegram.CommandType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.rgalim.gptbot.utils.TelegramUtils.START_COMMAND_TEXT;
import static com.rgalim.gptbot.utils.TestConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TelegramCommandServiceTest {

    @Mock
    private TelegramApiClient telegramApiClient;

    @InjectMocks
    private TelegramCommandService telegramCommandService;

    @Nested
    class IsCommand {

        @Test
        void whenTextMessageIsNotCommandThenReturnFalse() {
            boolean isCommand = telegramCommandService.isCommand("Text");

            assertFalse(isCommand);
        }

        @Test
        void whenTextMessageIsCommandThenReturnTrue() {
            boolean isCommand = telegramCommandService.isCommand("/start");

            assertTrue(isCommand);
        }
    }

    @Nested
    class HandleCommand {

        @Test
        void whenStartCommandThenHandle() {
            Command command = new Command(CommandType.START, "123", "text");

            when(telegramApiClient.sendMessage(START_COMMAND_TEXT, "123")).thenReturn(Mono.just(MESSAGE));

            StepVerifier.create(telegramCommandService.handleCommand(command))
                    .verifyComplete();
        }

        @Test
        void whenHelpCommandThenHandle() {
            Command command = new Command(CommandType.HELP, "123", "text");

            StepVerifier.create(telegramCommandService.handleCommand(command))
                    .verifyComplete();

            verifyNoInteractions(telegramApiClient);
        }

        @Test
        void whenSettingsCommandThenHandle() {
            Command command = new Command(CommandType.SETTINGS, "123", "text");

            StepVerifier.create(telegramCommandService.handleCommand(command))
                    .verifyComplete();

            verifyNoInteractions(telegramApiClient);
        }
    }
}