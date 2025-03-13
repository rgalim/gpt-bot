package com.rgalim.gptbot.service;

import com.rgalim.gptbot.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public Mono<Boolean> isValidUser(String userId) {
        Set<String> users = userRepository.getUsers();
        if (CollectionUtils.isEmpty(users)) {
            log.warn("The list of registered users is empty");
            return Mono.just(false);
        }
        return Mono.just(users.contains(userId));
    }
}
