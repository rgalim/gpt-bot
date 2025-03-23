package com.rgalim.gptbot.config.properties;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "web")
public record WebClientProperties(
        @NonNull Integer maxIdleTime,
        @NonNull Integer maxLifeTime
) {
}
