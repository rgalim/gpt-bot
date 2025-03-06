package com.rgalim.gptbot.model.telegram;

public record SendMessageResponse(
        boolean ok,
        Message result
) {
}
