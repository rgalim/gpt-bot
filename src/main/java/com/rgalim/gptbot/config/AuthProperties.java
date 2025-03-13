package com.rgalim.gptbot.config;

import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;

@ConfigurationProperties(prefix = "auth")
public record AuthProperties(
        @NonNull Set<String> users
) {
}
