package com.example.end.security.sec_servivce;


import com.example.end.models.User;
import com.example.end.repository.UserRepository;
import com.example.end.security.sec_dto.AuthInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private UserRepository userRepository;
    private final SecretKey accessKey;
    private final SecretKey refreshKey;


    public TokenService(
            @Value("${jwt.access.key}") String accessKey,
            @Value("${jwt.refresh.key}") String refreshKey

    ) {
        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessKey));
        this.refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(refreshKey));

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
                .claim("roles", user.getRole())
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
        String rolesString = (String) claims.get("roles");

        // Предположим, что роли разделены запятыми
        List<String> rolesList = Arrays.asList(rolesString.split(","));

        Set<User.Role> roles = new HashSet<>();
        for (String roleName : rolesList) {
            User.Role role = User.Role.valueOf(roleName);
            roles.add(role);
        }

        Set<String> roleStrings = roles.stream()
                .map(Enum::name)
                .collect(Collectors.toSet());

        return new AuthInfo(username, roleStrings);
    }


//    public AuthInfo generateAuthInfo(Claims claims) {
//        String username = claims.getSubject();
//        String rolesString  = claims.get("roles", String.class);
//        //List<LinkedHashMap<String, String>> rolesList = (List<LinkedHashMap<String, String>>) claims.get("roles");
//        Set<User.Role> roles = new HashSet<>();
//
//       if (rolesString != null && !rolesString.isEmpty()) {
//            String roleName = roleEntry.get("authority");
//            User.Role role = User.Role.valueOf(roleName);
//            roles.add(role);
//        }
//
//        // Преобразуем Set<User.Role> в Set<String>
//        Set<String> roleStrings = roles.stream()
//                .map(Enum::name)
//                .collect(Collectors.toSet());
//
//        return new AuthInfo(username, roleStrings);
//    }

//    public AuthInfo generateAuthInfo(Claims claims) {
//        String username = claims.getSubject();
//        String rolesString = claims.get("roles", String.class); // Получить роли как строку
//        Set<String> roles = new HashSet<>();
//
//        if (rolesString != null && !rolesString.isEmpty()) {
//            // Предположим, что роли разделены запятой
//            String[] rolesArray = rolesString.split(",");
//            for (String role : rolesArray) {
//                roles.add(role.trim()); // Удалить лишние пробелы
//            }
//        }
//
//        return new AuthInfo(username, roles);
//    }


}