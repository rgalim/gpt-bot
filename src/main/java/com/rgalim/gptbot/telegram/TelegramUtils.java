package com.rgalim.gptbot.telegram;

public final class TelegramUtils {

    private TelegramUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String GET_UPDATES_ENDPOINT = "/getUpdates";
    public static final String OFFSET_PARAM = "offset";
    public static final String TIMEOUT_PARAM = "timeout";

}
