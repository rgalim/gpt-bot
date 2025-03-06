package com.rgalim.gptbot.service;

import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.Command;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.rgalim.gptbot.utils.TelegramUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramCommandService {

    private final TelegramApiClient telegramApiClient;

    public boolean isCommand(String text) {
        return text.startsWith("/");
    }

    public Mono<Void> handleCommand(Command command) {
        return switch (command.commandType()) {
            case START -> handleStartCommand(command);
            case HELP -> handleHelpCommand();
            case SETTINGS -> handleSettingsCommand();
        };
    }

    private Mono<Void> handleStartCommand(Command command) {
        String from = command.from();
        log.info("Handling START command from {}", from);

        return telegramApiClient.sendMessage(START_COMMAND_TEXT, from)
                .then();
    }

    private Mono<Void> handleHelpCommand() {
        log.warn("Command /help is not implemented");
        return Mono.empty();
    }

    private Mono<Void> handleSettingsCommand() {
        log.warn("Command /settings is not implemented");
        return Mono.empty();
    }
}
