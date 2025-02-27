package com.rgalim.gptbot.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rgalim.gptbot.config.TelegramProperties;
import com.rgalim.gptbot.model.telegram.*;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class TelegramApiClientTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final AtomicLong LAST_ID = new AtomicLong(0);

    private static MockWebServer mockWebServer;

    private TelegramApiClient telegramApiClient;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void shutDown() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void initialize() {
        String url = String.format("http://localhost:%s", mockWebServer.getPort());
        TelegramProperties properties = new TelegramProperties(url, "12345:abcd", 1, 1, 100);
        telegramApiClient = new TelegramApiClient(WebClient.create(url), properties);
    }

    @Nested
    class FetchBotUpdates {

        @Test
        void whenSuccessfulResponseFromTelegramApiThenReturnUpdatesResponse() throws Exception {
            Message message = new Message(
                    1,
                    new User(1, false, "FirstName", "LastName", "username"),
                    12345,
                    "Message text");
            List<Update> updates = List.of(new Update(1, message));
            UpdatesResponse updatesResponse = new UpdatesResponse(true, updates);

            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setBody(OBJECT_MAPPER.writeValueAsString(updatesResponse)));

            StepVerifier.create(telegramApiClient.fetchBotUpdates(LAST_ID))
                    .expectNext(updatesResponse)
                    .verifyComplete();

            RecordedRequest recordedRequest = mockWebServer.takeRequest();

            assertEquals("GET", recordedRequest.getMethod());
            assertEquals("/bot12345:abcd/getUpdates?offset=1&timeout=1", recordedRequest.getPath());
        }

        @Test
        void whenFailedRequestWithTelegramApiExceptionThenRetry() throws Exception {
            Message message = new Message(
                    1,
                    new User(1, false, "FirstName", "LastName", "username"),
                    12345,
                    "Message text");
            List<Update> updates = List.of(new Update(1, message));
            UpdatesResponse updatesResponse = new UpdatesResponse(true, updates);

            TelegramErrorResponse telegramErrorResponse = new TelegramErrorResponse(false, 500, "Something went wrong");

            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setBody(OBJECT_MAPPER.writeValueAsString(telegramErrorResponse))
                    .setResponseCode(500));

            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setBody(OBJECT_MAPPER.writeValueAsString(updatesResponse))
                    .setResponseCode(200));

            StepVerifier.create(telegramApiClient.fetchBotUpdates(LAST_ID))
                    .expectNext(updatesResponse)
                    .verifyComplete();

            RecordedRequest recordedRequest = mockWebServer.takeRequest();

            assertEquals("GET", recordedRequest.getMethod());
            assertEquals("/bot12345:abcd/getUpdates?offset=1&timeout=1", recordedRequest.getPath());
        }

        @Test
        void whenAllRetryAttemptsFailedThenReturnEmptyMono() throws Exception {
            TelegramErrorResponse telegramErrorResponse = new TelegramErrorResponse(false, 500, "Something went wrong");

            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setBody(OBJECT_MAPPER.writeValueAsString(telegramErrorResponse))
                    .setResponseCode(500));

            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setBody(OBJECT_MAPPER.writeValueAsString(telegramErrorResponse))
                    .setResponseCode(500));

            StepVerifier.create(telegramApiClient.fetchBotUpdates(LAST_ID))
                    .verifyComplete();

            RecordedRequest recordedRequest = mockWebServer.takeRequest();

            assertEquals("GET", recordedRequest.getMethod());
            assertEquals("/bot12345:abcd/getUpdates?offset=1&timeout=1", recordedRequest.getPath());
        }

        @Test
        void whenUnexpectedResponseThenReturnEmptyMono() throws Exception {
            mockWebServer.enqueue(new MockResponse()
                    .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                    .setBody("Unexpected response"));

            StepVerifier.create(telegramApiClient.fetchBotUpdates(LAST_ID))
                    .verifyComplete();

            RecordedRequest recordedRequest = mockWebServer.takeRequest();

            assertEquals("GET", recordedRequest.getMethod());
            assertEquals("/bot12345:abcd/getUpdates?offset=1&timeout=1", recordedRequest.getPath());
        }
    }
}