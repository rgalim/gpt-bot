package com.rgalim.gptbot.controller;

import com.rgalim.gptbot.exception.UserValidationException;
import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.service.AuthService;
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
    private final AuthService authService;
    private final TelegramBotService telegramBotService;

    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Void> sendUpdate(@RequestHeader(BOT_AUTH_HEADER) String secretToken,
                                 @RequestBody Update updateRequest) {

        String userId = String.valueOf(updateRequest.message().from().id());

        return tokenValidator.validateToken(secretToken)
                .then(Mono.defer(() -> authService.isValidUser(userId)))
                .flatMap(isValidUser -> Boolean.TRUE.equals(isValidUser)
                        ? telegramBotService.handleUpdate(updateRequest)
                        : Mono.error(new UserValidationException(String.format("User %s is not authorized", userId))));
    }
}
