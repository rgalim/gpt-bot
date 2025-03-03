package com.rgalim.gptbot.service;

import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramCommandService {

    private final TelegramApiClient telegramApiClient;

    public boolean isCommand(String text) {
        return text.startsWith("/");
    }

    public void handleCommand(Command command) {
        switch (command) {
            case START -> handleStartCommand();
            case HELP -> handleHelpCommand();
            case SETTINGS -> handleSettingsCommand();
            default -> log.warn("Received unsupported command: {}", command);
        }
    }

    private void handleStartCommand() {
        log.warn("Command /start is not implemented");
    }

    private void handleHelpCommand() {
        log.warn("Command /help is not implemented");
    }

    private void handleSettingsCommand() {
        log.warn("Command /settings is not implemented");
    }
}
