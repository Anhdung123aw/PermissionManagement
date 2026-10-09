package com.example.PermissionManagement.sercurity;

import com.example.PermissionManagement.entity.EndpointEntity;
import com.example.PermissionManagement.repository.EndpointRepository;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DynamicAuthorizationFilter extends OncePerRequestFilter {
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final EndpointRepository endpointRepository;
    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 1. Bo qua cac endpoint public
//        if (path.startsWith("/auth/") || (path.equals("/users") && "POST".equalsIgnoreCase(method))) {
//            filterChain.doFilter(request, response);
//            return;
//        }

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
            return;
        }

        // 4. Lay danh sach cau hinh endpoint theo HTTP Method tu DB
        List<EndpointEntity> endpoints = endpointRepository.findByHttpMethod(method);

        // Tim xem API hien tai yeu cau nhung quyen (permissions) nao (gom vao Set)
        Set<String> requiredPermissions = new HashSet<>();
        for (EndpointEntity ep : endpoints) {
            if (pathMatcher.match(ep.getUrlPattern(), path)) {
                if (ep.getPermissions() != null && !ep.getPermissions().isEmpty()) {
                    requiredPermissions.addAll(ep.getPermissions());
                }
            }
        }

        // Neu endpoint nay khong yeu cau quyen dac biet nao -> cho qua
        if (requiredPermissions.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Lay danh sach permissions cua user tu Redis RAM
        String username = auth.getName();
        Set<String> userPermissions = redisTokenService.getUserPermissions(username);

        // 6. So khop quyen (Logic OR: Nguoi dung chi can so huu it nhat 1 quyen trong requiredPermissions)
        if (userPermissions != null && requiredPermissions.stream().anyMatch(userPermissions::contains)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 7. Thieu quyen -> Tra ve HTTP 403 Forbidden
        log.warn("User {} bi tu choi truy cap {} {}: Yeu cau mot trong cac quyen {}", username, method, path, requiredPermissions);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 403, \"message\": \"Ban khong co quyen truy cap chuc nang nay!\"}");
    }
}