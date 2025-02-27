package com.rgalim.gptbot.service;


import com.rgalim.gptbot.client.TelegramApiClient;
import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.model.telegram.UpdatesResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService {

    private final TelegramApiClient telegramApiClient;

    private final AtomicLong lastUpdateId = new AtomicLong(0);

    public Flux<UpdatesResponse> getUpdates() {
        return Mono.defer(() -> telegramApiClient.fetchBotUpdates(lastUpdateId))
                .doOnNext(this::updateOffset)
                .repeat();
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
}
