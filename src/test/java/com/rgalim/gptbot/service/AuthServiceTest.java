package com.rgalim.gptbot.service;

import com.rgalim.gptbot.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void whenUserIsValidThenReturnTrue() {
        when(userRepository.getUsers()).thenReturn(Set.of("1234", "5678"));

        boolean isValidUser = authService.isValidUser("1234");
        assertTrue(isValidUser);
    }

    @Test
    void whenUserIsInvalidThenReturnFalse() {
        when(userRepository.getUsers()).thenReturn(Set.of("1234"));

        boolean isValidUser = authService.isValidUser("5678");
        assertFalse(isValidUser);
    }

    @Test
    void whenRegisteredUsersAreEmptyThenReturnFalse() {
        when(userRepository.getUsers()).thenReturn(Set.of());

        boolean isValidUser = authService.isValidUser("1234");
        assertFalse(isValidUser);
    }
}