package com.rgalim.gptbot.service;

import com.openai.client.OpenAIClientAsync;
import com.openai.core.http.Headers;
import com.openai.errors.InternalServerException;
import com.openai.errors.OpenAIError;
import com.openai.errors.OpenAIServiceException;
import com.openai.models.ChatModel;
import com.openai.models.responses.*;
import com.openai.services.async.ResponseServiceAsync;
import com.rgalim.gptbot.config.properties.OpenAiProperties;
import com.rgalim.gptbot.exception.PromptValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.rgalim.gptbot.utils.TestConstants.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenAiServiceTest {

    @Mock
    private OpenAIClientAsync openAIClient;

    @Mock
    private ResponseServiceAsync responseServiceAsync;

    private OpenAiService openAiService;

    @BeforeEach
    void setUp() {
        OpenAiProperties openAiProperties = new OpenAiProperties("apiKey", 3L);
        openAiService = new OpenAiService(openAIClient, openAiProperties);
    }

    @Test
    void whenSentPromptSuccessfullyThenReturnOutputText() {
        ResponseOutputText responseOutputText = ResponseOutputText.builder()
                .text(OUTPUT_TEXT)
                .annotations(List.of())
                .build();
        ResponseOutputMessage.Content content = ResponseOutputMessage.Content.ofOutputText(responseOutputText);
        ResponseOutputMessage responseOutputMessage = ResponseOutputMessage.builder()
                .id("id")
                .content(List.of(content))
                .status(ResponseOutputMessage.Status.COMPLETED)
                .build();
        ResponseOutputItem responseOutputItem = ResponseOutputItem.ofMessage(responseOutputMessage);
        Response response = Response.builder()
                .id("id")
                .createdAt(1)
                .error(Optional.empty())
                .incompleteDetails(Optional.empty())
                .instructions(Optional.empty())
                .metadata(Optional.empty())
                .model(ChatModel.CHATGPT_4O_LATEST)
                .output(List.of(responseOutputItem))
                .parallelToolCalls(false)
                .temperature(1.0)
                .toolChoice(ToolChoiceOptions.AUTO)
                .tools(List.of())
                .topP(1.0)
                .build();

        when(openAIClient.responses()).thenReturn(responseServiceAsync);
        when(responseServiceAsync.create(RESPONSE_CREATE_PARAMS))
                .thenReturn(CompletableFuture.completedFuture(response));

        StepVerifier.create(openAiService.sendPrompt(INPUT_PROMPT))
                .expectNext(OUTPUT_TEXT)
                .verifyComplete();
    }

    @Test
    void whenFailedToSendPromptThenReturnError() {
        OpenAIServiceException exception = new InternalServerException(
                500, Headers.builder().build(), "Something went wrong", OpenAIError.builder().build()
        );

        when(openAIClient.responses()).thenReturn(responseServiceAsync);
        when(responseServiceAsync.create(RESPONSE_CREATE_PARAMS))
                .thenReturn(CompletableFuture.failedFuture(exception));

        StepVerifier.create(openAiService.sendPrompt(INPUT_PROMPT))
                .expectErrorMatches(error -> error instanceof OpenAIServiceException &&
                        error.getMessage().equals("500: OpenAIError{additionalProperties={}}"))
                .verify();
    }

    @Test
    void whenResponseDoesNotHaveOutputThenReturnEmptyMono() {
        Response responseWithoutOutput = Response.builder()
                .id("id")
                .createdAt(1)
                .error(Optional.empty())
                .incompleteDetails(Optional.empty())
                .instructions(Optional.empty())
                .metadata(Optional.empty())
                .model(ChatModel.CHATGPT_4O_LATEST)
                .output(List.of())
                .parallelToolCalls(false)
                .temperature(1.0)
                .toolChoice(ToolChoiceOptions.AUTO)
                .tools(List.of())
                .topP(1.0)
                .build();

        when(openAIClient.responses()).thenReturn(responseServiceAsync);
        when(responseServiceAsync.create(RESPONSE_CREATE_PARAMS))
                .thenReturn(CompletableFuture.completedFuture(responseWithoutOutput));

        StepVerifier.create(openAiService.sendPrompt(INPUT_PROMPT))
                .verifyComplete();
    }

    @Test
    void whenResponseDoesNotHaveMessageContentThenReturnEmptyMono() {
        ResponseOutputMessage responseOutputMessage = ResponseOutputMessage.builder()
                .id("id")
                .content(List.of())
                .status(ResponseOutputMessage.Status.COMPLETED)
                .build();
        ResponseOutputItem responseOutputItem = ResponseOutputItem.ofMessage(responseOutputMessage);
        Response responseWithoutMessageContent = Response.builder()
                .id("id")
                .createdAt(1)
                .error(Optional.empty())
                .incompleteDetails(Optional.empty())
                .instructions(Optional.empty())
                .metadata(Optional.empty())
                .model(ChatModel.CHATGPT_4O_LATEST)
                .output(List.of(responseOutputItem))
                .parallelToolCalls(false)
                .temperature(1.0)
                .toolChoice(ToolChoiceOptions.AUTO)
                .tools(List.of())
                .topP(1.0)
                .build();

        when(openAIClient.responses()).thenReturn(responseServiceAsync);
        when(responseServiceAsync.create(RESPONSE_CREATE_PARAMS))
                .thenReturn(CompletableFuture.completedFuture(responseWithoutMessageContent));

        StepVerifier.create(openAiService.sendPrompt(INPUT_PROMPT))
                .verifyComplete();
    }

    @Test
    void whenInvalidPromptThenReturnErrorMono() {
        String invalidPrompt = "Invalid prompt that exceeds limitation";
        StepVerifier.create(openAiService.sendPrompt(invalidPrompt))
                .expectErrorMatches(error -> error instanceof PromptValidationException &&
                        error.getMessage().equals("Number of tokens exceeds limitation"))
                .verify();

        verifyNoInteractions(openAIClient);
    }
}