package com.example.PermissionManagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisTokenService {
    private final StringRedisTemplate redisTemplate;
    private static final String TOKEN_KEY_PREFIX = "token:";
    private static final String PERMS_KEY_PREFIX = "user_perms:";
    private String getKey(String username) {
        return TOKEN_KEY_PREFIX + username;
    }
    public void saveToken(String username,String token,long durationInSeconds){
        try {
            redisTemplate.opsForValue().set(
                    getKey(username),
                    token,
                    Duration.ofSeconds(durationInSeconds)
            );
        } catch (Exception e) {
            log.error("Loi khi luu token vao Redis: {}", e.getMessage());
        }
    }
    // Kiem tra token co phai dang active kh
    public boolean isValidToken(String username, String token) {
        try {
            String activeToken = redisTemplate.opsForValue().get(getKey(username));
            return StringUtils.hasText(activeToken) && activeToken.equals(token);
        } catch (Exception e) {
            log.error("Loi khi tra token trong Redis: {}", e.getMessage());
            return false;
        }
    }
    // Xoa token khoi redis khi logout
    public void deleteToken(String username) {
        try {
            redisTemplate.delete(getKey(username));
            redisTemplate.delete(PERMS_KEY_PREFIX + username);
        } catch (Exception e) {
            log.error("Loi khi xoa token trong Redis: {}", e.getMessage());
        }
    }
    // Luu danh sach Permissions cua User vao Redis
    public void saveUserPermissions(String username, Set<String> permissions, long durationInSeconds){
        try{
            if(permissions != null && !permissions.isEmpty()){
                String permsString = String.join(",",permissions);
                redisTemplate.opsForValue().set(
                        PERMS_KEY_PREFIX + username,
                        permsString,
                        Duration.ofSeconds(durationInSeconds)
                );
            }
        }
        catch (Exception e){
            log.error("Loi khi luu per cua user vao Redis: {}",e.getMessage());
        }
    }
    // Lay danh sach Per cua user ttrong redis
    public Set<String> getUserPermissions(String username){
        try{
            String permsString = redisTemplate.opsForValue().get(PERMS_KEY_PREFIX + username);
            if(StringUtils.hasText(permsString)){
                return Set.of(permsString.split(","));
            }

        }catch (Exception e) {
            log.error("Loi khi lay permissions trong Redis: {}", e.getMessage());
        }
        return Collections.emptySet();
    }
    public void deleteUserPermissions(String username) {
        try {
            redisTemplate.delete(PERMS_KEY_PREFIX + username);
        } catch (Exception e) {
            log.error("Loi khi xoa permissions khoi Redis: {}", e.getMessage());
        }
    }
}
