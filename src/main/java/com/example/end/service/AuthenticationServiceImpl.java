package com.example.end.service;

import com.example.end.dto.LoginRequestDto;
import com.example.end.models.User;
import com.example.end.infrastructure.security.sec_dto.AuthInfo;
import com.example.end.infrastructure.security.sec_dto.TokenResponseDto;
import com.example.end.infrastructure.security.sec_servivce.TokenService;
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

/**
 * Implementation of the AuthenticationService interface.
 * Provides methods for user authentication, token generation, and validation.
 */
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserServiceImpl userService;
    private final Map<String, String> refreshStorage;
    private final BCryptPasswordEncoder encoder;
    private final TokenService tokenService;

    /**
     * Constructor to initialize the AuthenticationServiceImpl with required dependencies.
     *
     * @param userService the service to interact with user data
     * @param encoder the password encoder used for password verification
     * @param tokenService the service used to handle token generation and validation
     */
    @Autowired
    public AuthenticationServiceImpl(UserServiceImpl userService, BCryptPasswordEncoder encoder, TokenService tokenService) {
        this.userService = userService;
        this.refreshStorage = new HashMap<>();
        this.encoder = encoder;
        this.tokenService = tokenService;
    }

    /**
     * Authenticates a user based on their login credentials and generates JWT tokens (access and refresh).
     *
     * @param loginRequest the login request containing the user's email and password
     * @return a TokenResponseDto containing the access and refresh tokens
     * @throws AuthException if the user is not found or the password is incorrect
     */
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

    /**
     * Retrieves a new access token using a valid refresh token.
     *
     * @param refreshToken the refresh token used to generate a new access token
     * @return a TokenResponseDto containing the new access token, or null if the refresh token is invalid
     */
    public TokenResponseDto getAccessToken(@Nonnull String refreshToken) {
        // Validate the refresh token
        if (tokenService.validateRefreshToken(refreshToken)) {
            Claims refreshClaims = tokenService.getRefreshClaims(refreshToken);
            String email = refreshClaims.getSubject();

            Optional<User> optionalUser = userService.findByEmail(email);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                String accessToken = tokenService.generateAccessToken(user);
                return new TokenResponseDto(accessToken, null);
            }
        }

        return new TokenResponseDto(null, null);
    }

    /**
     * Retrieves the authentication information from the current security context.
     *
     * @return the authentication information as an AuthInfo object
     */
    public AuthInfo getAuthInfo() {
        return (AuthInfo) SecurityContextHolder.getContext().getAuthentication();
    }
}
