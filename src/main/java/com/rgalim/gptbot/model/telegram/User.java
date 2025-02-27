package com.rgalim.gptbot.model.telegram;

public record User(
        Integer id,
        boolean isBot,
        String firstName,
        String lastName,
        String username
) {
}
