package com.rgalim.gptbot.domain;

import com.rgalim.gptbot.config.AuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final AuthProperties properties;

    /*
        Could be replaced with DB
     */
    public Set<String> getUsers() {
        return properties.users();
    }
}
