package com.rgalim.gptbot;

import com.rgalim.gptbot.config.AuthProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AuthProperties.class)
public class GptBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(GptBotApplication.class, args);
    }

}
