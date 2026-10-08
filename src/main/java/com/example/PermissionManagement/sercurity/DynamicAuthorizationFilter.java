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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        String path = request.getRequestURI();
        String method =  request.getMethod();
        // bo qua ep public
        if(path.startsWith("/auth/") || (path.equals("/users") && "POST".equalsIgnoreCase(method))){
            filterChain.doFilter(request,response);
            return;
        }
        // Lay thong tin user dc JWTFilter xac thuc
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return;
        }
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin) {
            filterChain.doFilter(request, response);
        }
        List<EndpointPermissionEntity> endpoints = endpointPermissionRepository.findByHttpMethod(method);


        // Tim xem APi hien tai (Method ,URL) yeu cau ma quyen nao
        String requiredPermission = null ;
        for(EndpointPermissionEntity ep : endpoints){
            if(pathMatcher.match(ep.getUrlPattern(),path)){
                requiredPermission = ep.getPermissionCode();
                break;
            }
        }
        if (requiredPermission == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lay danh sach Per cua user tu Redis
        String username = auth.getName();
        Set<String> userPermissions = redisTokenService.getUserPermissions(username);

        // so quyen
        if(userPermissions != null && userPermissions.contains(requiredPermission)){
            filterChain.doFilter(request,response);
            return;
        }
        log.warn("User {} ban tao chua co quyen {} {}: Thieu quyen {}", username, method, path, requiredPermission);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\": 403, \"message\": \"Ban kh co quyen truy cap chuc nang nay!\"}");
    }
}
