package com.rgalim.gptbot.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        @NonNull String url,
        @NonNull String token,
        @NonNull Integer timeout
) {
}
