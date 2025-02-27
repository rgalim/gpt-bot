package com.rgalim.gptbot.model.telegram;


public record Message(
        Integer messageId,
        User from,
        Integer date,
        String text
) {
}
