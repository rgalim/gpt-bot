package com.rgalim.gptbot.model.telegram;


public record TelegramErrorResponse(
        boolean ok,
        Integer errorCode,
        String description
) {
}
