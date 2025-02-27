package com.rgalim.gptbot.exception;

public class TelegramApiException extends RuntimeException {
    public TelegramApiException(String message) {
        super(message);
    }
}
