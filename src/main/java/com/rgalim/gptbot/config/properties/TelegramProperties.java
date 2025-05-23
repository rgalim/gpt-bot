package com.rgalim.gptbot.config.properties;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "telegram")
public record TelegramProperties(
        @NonNull String url,
        @NonNull String botToken,
        @NonNull String botSecret,
        @NonNull Integer timeout,
        @NonNull Integer retryAttempts,
        @NonNull Integer retryBackoff
) {
}
