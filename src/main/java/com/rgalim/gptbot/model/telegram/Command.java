package com.rgalim.gptbot.model.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public enum Command {
    START("/start"),
    HELP("/help"),
    SETTINGS("/settings");

    private final String value;

    public static Optional<Command> from(String text) {
        return Arrays.stream(Command.values())
                .filter(command -> text.equalsIgnoreCase(command.getValue()))
                .findAny();
    }
}
