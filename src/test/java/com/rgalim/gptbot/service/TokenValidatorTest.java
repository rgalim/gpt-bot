package com.rgalim.gptbot.service;

import com.rgalim.gptbot.config.properties.TelegramProperties;
import com.rgalim.gptbot.exception.TokenValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;


class TokenValidatorTest {

    private TokenValidator tokenValidator;

    @BeforeEach
    void setUp() {
        TelegramProperties properties = new TelegramProperties("url", "12345:abcd", "secret", 1, 1, 100);
        tokenValidator = new TokenValidator(properties);
    }

    @Test
    void whenSecretTokenIsValidThenReturnToken() {
        StepVerifier.create(tokenValidator.validateToken("secret"))
                .expectNext("secret")
                .verifyComplete();
    }

    @Test
    void whenSecretTokenIsEmptyThenReturnError() {
        StepVerifier.create(tokenValidator.validateToken(""))
                .expectErrorMatches(error -> error instanceof TokenValidationException &&
                        error.getMessage().equals("Secret token must not be empty"))
                .verify();
    }

    @Test
    void whenSecretTokenIsInvalidThenReturnError() {
        StepVerifier.create(tokenValidator.validateToken("invalidToken"))
                .expectErrorMatches(error -> error instanceof TokenValidationException &&
                        error.getMessage().equals("Secret token is invalid"))
                .verify();
    }
}