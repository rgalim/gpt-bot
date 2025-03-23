package com.rgalim.gptbot.service;


import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramApiClient telegramApiClient;
    private final TelegramCommandService telegramCommandService;
    private final TelegramMessageService telegramMessageService;

    private final AtomicLong lastUpdateId = new AtomicLong(0);

    // Get updates method is used in the long-polling mode when webhook is not enabled
    public Flux<UpdatesResponse> getUpdates() {
        return Mono.defer(() -> telegramApiClient.fetchBotUpdates(lastUpdateId))
                .doOnNext(this::updateOffset)
                .repeat();
    }

    public Mono<Void> handleUpdate(Update update) {
        Message message = update.message();
        if (message != null && hasText(message.text())) {
            return telegramCommandService.isCommand(message.text())
                    ? handleCommand(message)
                    : telegramMessageService.handleMessage(message);
        }
        return Mono.empty();
    }

    private void updateOffset(UpdatesResponse response) {
        List<Update> updates = response.result();
        if (updates != null && !updates.isEmpty()) {
            long newOffset = updates.stream()
                    .mapToLong(Update::updateId)
                    .max()
                    .orElse(lastUpdateId.get());
            lastUpdateId.set(newOffset);
        }
    }

    private Mono<Void> handleCommand(Message message) {
        return CommandType.from(message.text())
                .map(commandType -> {
                    String userId = String.valueOf(message.from().id());
                    Command command = new Command(commandType, userId, message.text());
                    return telegramCommandService.handleCommand(command);
                })
                .orElseGet(() -> {
                    log.warn("Command {} is not supported", message.text());
                    return Mono.empty();
                });
    }
}
