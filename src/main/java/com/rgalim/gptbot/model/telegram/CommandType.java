package com.rgalim.gptbot.model.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum CommandType {
    START("/start"),
    HELP("/help"),
    SETTINGS("/settings");

    private final String value;

    public static Optional<CommandType> from(String text) {
        return Arrays.stream(CommandType.values())
                .filter(command -> text.equalsIgnoreCase(command.getValue()))
                .findAny();
    }
}
