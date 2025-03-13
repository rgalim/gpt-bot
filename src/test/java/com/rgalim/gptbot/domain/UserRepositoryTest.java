package com.rgalim.gptbot.domain;

import com.rgalim.gptbot.config.AuthProperties;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


class UserRepositoryTest {

    @Test
    void whenUsersExistThenReturnSet() {
        AuthProperties properties = new AuthProperties(Set.of("1234", "5678"));
        UserRepository userRepository = new UserRepository(properties);

        Set<String> users = userRepository.getUsers();

        assertEquals(Set.of("1234", "5678"), users);
    }
}