package com.rgalim.gptbot.model.telegram;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    void whenValidCommandThenCreateFromText() {

        Optional<Command> command = Command.from("/start");

        assertTrue(command.isPresent());
    }

    @Test
    void whenInvalidCommandThenReturnEmptyOptional() {

        Optional<Command> command = Command.from("/unknown");

        assertFalse(command.isPresent());
    }
}