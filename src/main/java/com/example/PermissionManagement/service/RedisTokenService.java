package com.example.PermissionManagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenService {
    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_KEY_PREFIX = "token:";
    private String getKey(String username) {
        return TOKEN_KEY_PREFIX + username;
    }
    // Luu token vao redis khi dn
    public void saveToken(String username,String token,long durationInSeconds){
        try {
            redisTemplate.opsForValue().set(
                    getKey(username),
                    token,
                    Duration.ofSeconds(durationInSeconds)
            );
        } catch (Exception e) {
            log.error("Lỗi khi lưu token vào Redis: {}", e.getMessage());
        }
    }
    //Kiểm tra token có phải token đang active hay không
    public boolean isValidToken(String username, String token) {
        try {
            String activeToken = redisTemplate.opsForValue().get(getKey(username));
            return StringUtils.hasText(activeToken) && activeToken.equals(token);
        } catch (Exception e) {
            log.error("Lỗi khi kiểm tra token trong Redis: {}", e.getMessage());
            return false;
        }
    }
    // Xoa token khoi redis khi logout
    public void deleteToken(String username) {
        try {
            redisTemplate.delete(getKey(username));
        } catch (Exception e) {
            log.error("Lỗi khi xóa token trong Redis: {}", e.getMessage());
        }
    }


}
