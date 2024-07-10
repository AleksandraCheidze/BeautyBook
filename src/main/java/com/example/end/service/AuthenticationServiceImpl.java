package com.example.end.service;

import com.example.end.dto.LoginRequestDto;
import com.example.end.models.User;
import com.example.end.security.sec_dto.AuthInfo;
import com.example.end.security.sec_dto.TokenResponseDto;
import com.example.end.security.sec_servivce.TokenService;
import com.example.end.service.interfaces.AuthenticationService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserServiceImpl userService;
    private final Map<String, String> refreshStorage;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;

    @Autowired
    public AuthenticationServiceImpl( UserServiceImpl userService, BCryptPasswordEncoder encoder,TokenService tokenService) {
        this.userService = userService;
        this.refreshStorage = new HashMap<>();
        this.encoder = encoder;
        this.tokenService = tokenService;
    }



    public TokenResponseDto login(@Nonnull LoginRequestDto loginRequest) throws AuthException {
        String email = loginRequest.getEmail();
        Optional<User> userOptional = userService.findByEmail(email);

        if (userOptional.isPresent()) {
            User foundUser = userOptional.get();

            if (encoder.matches(loginRequest.getHashPassword(), foundUser.getHashPassword())) {
                String accessToken = tokenService.generateAccessToken(foundUser);
                String refreshToken = tokenService.generateRefreshToken(foundUser);
                refreshStorage.put(email, refreshToken);
                return new TokenResponseDto(accessToken, refreshToken);
            } else {
                throw new AuthException("Password is incorrect");
            }
        } else {
            throw new AuthException("User not found for email: " + email);
        }
    }


    public TokenResponseDto getAccessToken(@Nonnull String refreshToken) {
        if (tokenService.validateRefreshToken(refreshToken)) {
            Claims refreshClaims = tokenService.getRefreshClaims(refreshToken);
            String email = refreshClaims.getSubject();
            Optional<User> optionalUser = userService.loadUserByEmail(email);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String accessToken = tokenService.generateAccessToken(user);
                return new TokenResponseDto(accessToken, null);
            }
        }
        // Обработка случая, когда пользователь не найден
        return new TokenResponseDto(null, null);
    }



    public AuthInfo getAuthInfo() {
        return (AuthInfo) SecurityContextHolder.getContext().getAuthentication();
    }
}