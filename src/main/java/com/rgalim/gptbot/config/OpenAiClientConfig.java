package com.rgalim.gptbot.config;

import com.openai.client.OpenAIClientAsync;
import com.openai.client.okhttp.OpenAIOkHttpClientAsync;
import com.rgalim.gptbot.config.properties.OpenAiProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties(OpenAiProperties.class)
public class OpenAiClientConfig {

    @Bean
    public OpenAIClientAsync openAIClient(OpenAiProperties properties) {
        return OpenAIOkHttpClientAsync.builder()
                .apiKey(properties.apiKey())
                .organization(properties.organizationId())
                .project(properties.projectId())
                .build();
    }
}
