package com.rgalim.gptbot.service;

import com.rgalim.gptbot.config.TelegramProperties;
import com.rgalim.gptbot.exception.TokenValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class TokenValidator {

    private final TelegramProperties properties;

    public Mono<String> validateToken(String secretToken) {
        if (!StringUtils.hasText(secretToken)) {
            return Mono.error(new TokenValidationException("Secret token must not be empty"));
        }
        if (!properties.botSecret().equals(secretToken)) {
            return Mono.error(new TokenValidationException("Secret token is invalid"));
        }
        return Mono.just(secretToken);
    }
}
