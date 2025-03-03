package com.rgalim.gptbot.service;

import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.Command;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
            telegramCommandService.handleCommand(Command.START);

            verifyNoInteractions(telegramApiClient);
        }

        @Test
        void whenHelpCommandThenHandle() {
            telegramCommandService.handleCommand(Command.HELP);

            verifyNoInteractions(telegramApiClient);
        }

        @Test
        void whenSettingsCommandThenHandle() {
            telegramCommandService.handleCommand(Command.SETTINGS);

            verifyNoInteractions(telegramApiClient);
        }
    }
}