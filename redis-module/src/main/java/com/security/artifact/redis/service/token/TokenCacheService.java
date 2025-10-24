package com.security.artifact.redis.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Stores the token using the username as the key, overwriting any existing token for that user
    public void saveToken(String token, String username) {
        redisTemplate.opsForValue().set(username, token);
    }

    // Retrieves the stored token for the given username
    public String getTokenForUser(String username) {
        return (String) redisTemplate.opsForValue().get(username);
    }

    // Deletes the stored token for the given username
    public void deleteTokenForUser(String username) {
        redisTemplate.delete(username);
    }
}
