package com.rgalim.gptbot.model.telegram;

public record Command(
        CommandType commandType,
        String from,
        String text
) {
}
