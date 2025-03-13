package com.rgalim.gptbot.service;

import com.rgalim.gptbot.domain.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Set;

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

        StepVerifier.create(authService.isValidUser("1234"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void whenUserIsInvalidThenReturnFalse() {
        when(userRepository.getUsers()).thenReturn(Set.of("1234"));

        StepVerifier.create(authService.isValidUser("5678"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void whenRegisteredUsersAreEmptyThenReturnFalse() {
        when(userRepository.getUsers()).thenReturn(Set.of());

        StepVerifier.create(authService.isValidUser("1234"))
                .expectNext(false)
                .verifyComplete();
    }
}