package com.example.PermissionManagement.sercurity.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.web.util.ContentCachingResponseWrapper;
@Component
@Slf4j
@Order(1)
public class RequestCachingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{

        long startTime = System.currentTimeMillis();
        // Boc res,req
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String requestBody = cachedRequest.getReader()
                .lines()
                .reduce("",(accumulator,actual) -> accumulator+actual);
        try{
            // Cho req di vao filter sau,contr
            filterChain.doFilter(cachedRequest,responseWrapper);
        } finally {
            // doc req payload contr tra ve
            long duration = System.currentTimeMillis() - startTime;
            log.info("[HTTP AUDIT] {} {} | Status: {} | Time: {}ms}",
                    cachedRequest.getMethod(),
                    cachedRequest.getRequestURI(),
                    responseWrapper.getStatus(),
                    duration);
            responseWrapper.copyBodyToResponse();

        }
    }
}
