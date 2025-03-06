package com.rgalim.gptbot.utils;

import com.rgalim.gptbot.model.telegram.Message;
import com.rgalim.gptbot.model.telegram.Update;
import com.rgalim.gptbot.model.telegram.User;

public class TestConstants {

    public static final User USER =
            new User(1L, false, "FirstName", "LastName", "username");
    public static final Message MESSAGE = new Message(1, USER, 12345, "Message text");
    public static final Update UPDATE = new Update(1, MESSAGE);
}
