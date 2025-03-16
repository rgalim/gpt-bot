package com.rgalim.gptbot.utils;

import com.rgalim.gptbot.exception.PromptValidationException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class OpenAiUtils {

    private OpenAiUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final double TOKENS_PER_WORD = 1.33;
    private static final Pattern INVALID_CHARACTERS_PATTERN = Pattern.compile("[^a-zA-Z0-9 .,!?\"']");

    public static Mono<String> validatePrompt(String prompt, Long inputTokenLimit) {
        String[] words = prompt.split("\\s+");
        /*
             Rejects:
             - incorrect prompts with less than 2 words, e.g. malformed long-length text.
             - prompts with invalid characters
         */
        if (words.length < 2 || containsInvalidCharacters(prompt)) {
            return Mono.error(new PromptValidationException("The prompt input is invalid"));
        }
        /*
            In average, one word in a prompt is equal to 1.33 tokens.
            The number of tokens is limited per prompt to prevent spam requests
         */
        long tokenCount = Math.round(words.length * TOKENS_PER_WORD);
        if (tokenCount > inputTokenLimit) {
            return Mono.error(new PromptValidationException("Number of tokens exceeds limitation"));
        }
        return Mono.just(prompt);
    }

    private static boolean containsInvalidCharacters(String input) {
        Matcher matcher = INVALID_CHARACTERS_PATTERN.matcher(input);
        return matcher.find();
    }
}
