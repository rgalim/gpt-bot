package com.rgalim.gptbot.model.telegram;


public record Update(
        Integer updateId,
        Message message
) {
}
