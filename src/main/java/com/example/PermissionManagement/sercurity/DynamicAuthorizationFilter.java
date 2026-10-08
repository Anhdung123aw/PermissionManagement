package com.example.PermissionManagement.sercurity;

import com.example.PermissionManagement.entity.EndpointPermissionEntity;
import com.example.PermissionManagement.repository.EndpointPermissionRepository;
import com.example.PermissionManagement.service.RedisTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicAuthorizationFilter extends OncePerRequestFilter {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final EndpointPermissionRepository endpointPermissionRepository;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Bo qua cac endpoint public
        if (path.startsWith("/auth/") || (path.equals("/users") && "POST".equalsIgnoreCase(method))) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Lay thong tin user tu SecurityContextHolder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Fast-path cho ADMIN: Bat buoc phai co RETURN; ngay sau khi doFilter!
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            filterChain.doFilter(request, response);
            return; // <--- DUNG TAI DAY! Neu khong co return thi se bi goi doFilter 2 lan lam loi Double JSON!
        }

        // 4. Lay danh sach cau hinh endpoint theo method
        List<EndpointPermissionEntity> endpoints = endpointPermissionRepository.findByHttpMethod(method);

        // Tim xem API hien tai yeu cau permission nao
        String requiredPermission = null;
        for (EndpointPermissionEntity ep : endpoints) {
            if (pathMatcher.match(ep.getUrlPattern(), path)) {
                requiredPermission = ep.getPermissionCode();
                break;
            }
        }

        if (requiredPermission == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Lay danh sach permissions cua user tu Redis
        String username = auth.getName();
        Set<String> userPermissions = redisTokenService.getUserPermissions(username);

        // 6. So khop permission
        if (userPermissions != null && userPermissions.contains(requiredPermission)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.warn("User {} bi tu choi truy cap {} {}: Thieu quyen {}", username, method, path, requiredPermission);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 403, \"message\": \"Ban khong co quyen truy cap chuc nang nay!\"}");
    }
}