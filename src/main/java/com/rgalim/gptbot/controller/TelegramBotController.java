package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.service.TelegramBotService;
import com.rgalim.gptbot.service.TokenValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import static com.rgalim.gptbot.utils.TelegramUtils.BOT_AUTH_HEADER;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/telegram")
public class TelegramBotController {

    private final TokenValidator tokenValidator;
    private final TelegramBotService telegramBotService;

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> sendUpdate(@RequestHeader(BOT_AUTH_HEADER) String secretToken,
                                 @RequestBody Update updateRequest) {

        return tokenValidator.validateToken(secretToken)
                .then(Mono.defer(() -> telegramBotService.handleUpdate(updateRequest)));
    }
}
