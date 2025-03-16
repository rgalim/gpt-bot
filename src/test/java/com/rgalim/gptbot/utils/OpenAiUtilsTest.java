package com.rgalim.gptbot.utils;

import com.rgalim.gptbot.exception.PromptValidationException;
import org.junit.jupiter.api.Test;

import reactor.test.StepVerifier;

import static com.rgalim.gptbot.utils.OpenAiUtils.*;


class OpenAiUtilsTest {

    @Test
    void whenValidPromptThenReturnMonoWithPrompt() {
        String prompt = "Valid prompt message";
        Long tokenLimit = 10L;

        StepVerifier.create(validatePrompt(prompt, tokenLimit))
                .expectNext(prompt)
                .verifyComplete();
    }

    @Test
    void whenPromptWithOneWordThenReturnMonoError() {
        String prompt = "InvalidLongText";
        Long tokenLimit = 10L;

        StepVerifier.create(validatePrompt(prompt, tokenLimit))
                .expectErrorMatches(error -> error instanceof PromptValidationException &&
                        error.getMessage().equals("The prompt input is invalid"))
                .verify();
    }

    @Test
    void whenPromptWithInvalidCharacterThenReturnMonoError() {
        String prompt = "Prompt with invalid character @";
        Long tokenLimit = 10L;

        StepVerifier.create(validatePrompt(prompt, tokenLimit))
                .expectErrorMatches(error -> error instanceof PromptValidationException &&
                        error.getMessage().equals("The prompt input is invalid"))
                .verify();
    }

    @Test
    void whenPromptExceedsTokenLimitThenReturnMonoError() {
        String prompt = "Prompt that exceeds the token limit";
        Long tokenLimit = 3L;

        StepVerifier.create(validatePrompt(prompt, tokenLimit))
                .expectErrorMatches(error -> error instanceof PromptValidationException &&
                        error.getMessage().equals("Number of tokens exceeds limitation"))
                .verify();
    }
}