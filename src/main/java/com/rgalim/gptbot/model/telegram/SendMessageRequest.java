package com.rgalim.gptbot.model.telegram;


import com.fasterxml.jackson.annotation.JsonProperty;

public record SendMessageRequest(
        @JsonProperty("chat_id")
        String chatId,
        String text
) {
}
