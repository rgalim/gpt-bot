package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.service.TelegramBotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.rgalim.gptbot.utils.TelegramUtils.BOT_AUTH_HEADER;
import static com.rgalim.gptbot.utils.TelegramUtils.validateToken;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telegram")
public class TelegramBotController {

    private final TelegramBotService telegramBotService;

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> sendUpdate(@RequestHeader(BOT_AUTH_HEADER) String token,
                                 @RequestBody Update updateRequest) {

        validateToken(token);

        return telegramBotService.handleUpdate(updateRequest);
    }
}
