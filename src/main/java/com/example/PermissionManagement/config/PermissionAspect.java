package com.example.PermissionManagement.config;

import com.example.PermissionManagement.exception.AppException;
import com.example.PermissionManagement.exception.ErrorCode;
import com.example.PermissionManagement.service.RedisTokenService;
import io.netty.util.internal.ObjectUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class PermissionAspect {

    private final RedisTokenService redisTokenService;

    @Around("@annotation(hasPermissionCms)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, HasPermissionCms hasPermissionCms) throws Throwable {
        // Lay thong tin user tu SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        // Admin cho qua
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            return joinPoint.proceed();
        }
        String username = auth.getName();
        Set<String> userPermissions = redisTokenService.getUserPermissions(username);

        String[] requiredPermissions = hasPermissionCms.value();
        boolean hasRequiredPermission = Arrays.stream(requiredPermissions).anyMatch(userPermissions::contains);

        if (!hasRequiredPermission) {
            log.warn("User {} bị chặn khi gọi method {}. Yêu cầu một trong các quyền: {}",
                    username, joinPoint.getSignature().getName(), Arrays.toString(requiredPermissions));
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
            return joinPoint.proceed();
        }
    }

