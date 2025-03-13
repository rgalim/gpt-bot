package com.rgalim.gptbot.config;

import com.rgalim.gptbot.config.properties.TelegramProperties;
import com.rgalim.gptbot.config.properties.WebClientProperties;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({
        WebClientProperties.class,
        TelegramProperties.class
})
public class WebClientConfig {

    @Bean
    public WebClient webClient(WebClientProperties properties) {
        ConnectionProvider provider = ConnectionProvider
                .builder("webClientConnectionProvider")
                // Ensure maxIdleTime is longer than Telegram timeout value
                .maxIdleTime(Duration.ofSeconds(properties.maxIdleTime()))
                .maxLifeTime(Duration.ofSeconds(properties.maxLifeTime()))
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create(provider)
                                .resolver(DefaultAddressResolverGroup.INSTANCE)))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
