package com.rgalim.gptbot.service;

import com.openai.client.OpenAIClientAsync;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import com.rgalim.gptbot.config.properties.OpenAiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static com.rgalim.gptbot.utils.OpenAiUtils.validatePrompt;
import static org.springframework.util.CollectionUtils.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final OpenAIClientAsync openAiClient;
    private final OpenAiProperties openAiProperties;

    public Mono<String> sendPrompt(String prompt) {
        return validatePrompt(prompt, openAiProperties.inputTokenLimit())
                .map(this::mapToResponseCreateParams)
                .flatMap(params -> Mono.fromFuture(() -> openAiClient.responses().create(params)))
                .mapNotNull(this::mapToResponseText)
                .doOnSuccess(response -> log.info("Successfully got response from OpenAI: {}", response))
                .doOnError(error -> log.error("Failed to send prompt to OpenAI: {}", error.getMessage()));
    }

    private String mapToResponseText(Response response) {
        List<ResponseOutputItem> output = response.output();
        if (isEmpty(output)) {
            log.error("GPT output is empty");
            return null;
        }
        ResponseOutputItem responseOutputItem = output.get(0);
        return responseOutputItem.message()
                .flatMap(message -> {
                    List<ResponseOutputMessage.Content> contentList = message.content();
                    if (isEmpty(contentList)) {
                        log.error("GPT message content is empty");
                        return Optional.empty();
                    }
                    return contentList.get(0).outputText();
                })
                .map(ResponseOutputText::text)
                .orElse(null);
    }

    private ResponseCreateParams mapToResponseCreateParams(String prompt) {
        return ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_4O)
                .build();
    }
}
