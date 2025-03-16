package com.rgalim.gptbot.exception;

public class PromptValidationException extends RuntimeException {
    public PromptValidationException(String message) {
        super(message);
    }
}
