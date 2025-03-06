package com.rgalim.gptbot.model.telegram;

import com.fasterxml.jackson.annotation.JsonProperty;

public record User(
        Long id,
        @JsonProperty("is_bot")
        boolean isBot,
        @JsonProperty("first_name")
        String firstName,
        @JsonProperty("last_name")
        String lastName,
        String username
) {
}
