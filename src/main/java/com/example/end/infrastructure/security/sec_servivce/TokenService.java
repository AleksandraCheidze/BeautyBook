package com.example.end.infrastructure.security.sec_servivce;

import com.example.end.infrastructure.security.sec_dto.AuthInfo;
import com.example.end.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@Service
public class TokenService {
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final SecretKey accessKey;
    private final SecretKey refreshKey;

    public TokenService(
            @Value("${jwt.access.key}") String accessKeyBase64,
            @Value("${jwt.refresh.key}") String refreshKeyBase64) {
        logger.info("Initializing TokenService");
        logger.debug("Access key length: {}", accessKeyBase64 != null ? accessKeyBase64.length() : "null");
        logger.debug("Refresh key length: {}", refreshKeyBase64 != null ? refreshKeyBase64.length() : "null");
        
        try {
            if (accessKeyBase64 == null || refreshKeyBase64 == null) {
                throw new IllegalStateException("JWT keys are not properly configured");
            }
            
            this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKeyBase64));
            this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshKeyBase64));
            logger.info("TokenService initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize TokenService: {}", e.getMessage());
            throw e;
        }
    }

    public String generateAccessToken(@Nonnull User user) {
        LocalDateTime currentDate = LocalDateTime.now();
        Instant expirationInstant = currentDate.plusDays(1).atZone(ZoneId.systemDefault()).toInstant();
        Date expirationDate = Date.from(expirationInstant);

        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(expirationDate)
                .signWith(accessKey)
                .claim("user_id", user.getId())
                .claim("roles", user.getRole().name())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .claim("email", user.getEmail())
                .compact();
    }

    public String generateRefreshToken(@Nonnull User user) {
        LocalDateTime currentDate = LocalDateTime.now();
        Instant expirationInstant = currentDate.plusDays(14).atZone(ZoneId.systemDefault()).toInstant();
        Date expirationDate = Date.from(expirationInstant);

        return Jwts.builder()
                .subject(user.getEmail())
                .expiration(expirationDate)
                .signWith(refreshKey)
                .compact();
    }

    public boolean validateAccessToken(@Nonnull String accessToken) {
        return validateToken(accessToken, accessKey);
    }

    public boolean validateRefreshToken(@Nonnull String refreshToken) {
        return validateToken(refreshToken, refreshKey);
    }

    private boolean validateToken(@Nonnull String token, @Nonnull SecretKey key) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getAccessClaims(@Nonnull String accessToken) {
        return getClaims(accessToken, accessKey);
    }

    public Claims getRefreshClaims(@Nonnull String refreshToken) {
        return getClaims(refreshToken, refreshKey);
    }

    public Claims getClaims(@Nonnull String token, @Nonnull SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public AuthInfo generateAuthInfo(Claims claims) {
        String username = claims.getSubject();
        String roleString = claims.get("roles", String.class);
        User.Role role = User.Role.valueOf(roleString);
        return new AuthInfo(username, Set.of(role));
    }
}
