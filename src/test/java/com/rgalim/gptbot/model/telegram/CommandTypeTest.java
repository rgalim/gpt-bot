package com.rgalim.gptbot.model.telegram;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommandTypeTest {

    @Test
    void whenValidCommandThenCreateFromText() {

        Optional<CommandType> command = CommandType.from("/start");

        assertTrue(command.isPresent());
    }

    @Test
    void whenInvalidCommandThenReturnEmptyOptional() {

        Optional<CommandType> command = CommandType.from("/unknown");

        assertFalse(command.isPresent());
    }
}