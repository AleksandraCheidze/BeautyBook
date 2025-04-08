package com.example.end.service.interfaces;

import com.example.end.dto.LoginRequestDto;
import com.example.end.infrastructure.security.sec_dto.TokenResponseDto;
import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    TokenResponseDto getAccessToken(String refreshToken);

    TokenResponseDto login(LoginRequestDto loginRequest) throws AuthException;
}