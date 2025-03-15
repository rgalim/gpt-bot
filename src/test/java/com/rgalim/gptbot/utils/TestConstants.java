package com.rgalim.gptbot.utils;

import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.rgalim.gptbot.model.telegram.Message;
import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.model.telegram.User;

public class TestConstants {

    public static final User USER =
            new User(1L, false, "FirstName", "LastName", "username");
    public static final Message MESSAGE = new Message(1, USER, 12345, "Message text");
    public static final Update UPDATE = new Update(1, MESSAGE);
    public static final String SECRET_TOKEN = "secret";
    public static final String INPUT_PROMPT = "Input prompt";
    public static final String OUTPUT_TEXT = "Output text";
    public static final ResponseCreateParams RESPONSE_CREATE_PARAMS = ResponseCreateParams.builder()
            .input(INPUT_PROMPT)
            .model(ChatModel.GPT_4O)
            .build();
}
