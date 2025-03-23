package com.rgalim.gptbot.service;

import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramMessageService {

    private final OpenAiService openAiService;
    private final TelegramApiClient telegramApiClient;

    public Mono<Void> handleMessage(Message message) {
        String userId = String.valueOf(message.from().id());
        String text = message.text();
        log.info("Received message from user {}: {}", message.from().username(), text);

        return openAiService.sendPrompt(text)
                .flatMap(output -> telegramApiClient.sendMessage(output, userId))
                .then();
    }
}
