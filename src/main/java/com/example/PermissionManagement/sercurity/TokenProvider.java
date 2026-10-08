package com.example.PermissionManagement.sercurity;

import com.example.PermissionManagement.service.RedisTokenService;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenProvider {

    @Value("${jwt.signerKey}")
    private String signerKey;

    private final RedisTokenService redisTokenService;

    public TokenProvider(RedisTokenService redisTokenService) {
        this.redisTokenService = redisTokenService;
    }

    // 1. Tự lấy token từ Header Authorization
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // 2. Tự kiểm tra token: Chữ ký, hạn dùng và Redis
    public boolean validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(signerKey.getBytes());

            if (!signedJWT.verify(verifier)) {
                return false;
            }

            Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();
            if (expirationTime == null || expirationTime.before(new Date())) {
                return false;
            }

            // Check Redis: Xem token còn active không (đã logout chưa)
            String username = signedJWT.getJWTClaimsSet().getSubject();
            if (!redisTokenService.isValidToken(username, token)) {
                log.error("Token không tồn tại trong Redis hoặc đã logout");
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Lỗi khi validate token: {}", e.getMessage());
            return false;
        }
    }

    // 3. Tự bóc Claims và chuyển đổi thành Authentication cho Spring Security
    public Authentication getAuthentication(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            String username = claims.getSubject();
            String scope = claims.getStringClaim("scope");

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            if (StringUtils.hasText(scope)) {
                authorities = Arrays.stream(scope.split(" "))
                        .filter(StringUtils::hasText)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
            }

            return new UsernamePasswordAuthenticationToken(username, token, authorities);
        } catch (Exception e) {
            return null;
        }
    }
}
