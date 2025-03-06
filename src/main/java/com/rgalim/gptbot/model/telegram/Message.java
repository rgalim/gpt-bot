package com.rgalim.gptbot.model.telegram;


import com.fasterxml.jackson.annotation.JsonProperty;

public record Message(
        @JsonProperty("message_id")
        Integer messageId,
        User from,
        Integer date,
        String text
) {
}
