package com.rgalim.gptbot.config.properties;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai")
public record OpenAiProperties(
        @NonNull String apiKey,
        @NonNull Integer inputTokenLimit
) {
}
