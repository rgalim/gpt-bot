package com.rgalim.gptbot.model.telegram;


import com.fasterxml.jackson.annotation.JsonProperty;

public record Update(
        @JsonProperty("update_id")
        Integer updateId,
        Message message
) {
}
