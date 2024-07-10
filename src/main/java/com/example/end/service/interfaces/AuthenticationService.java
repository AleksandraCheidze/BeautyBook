package com.example.end.service.interfaces;

import com.example.end.dto.LoginRequestDto;
import com.example.end.dto.UserDto;
import com.example.end.security.sec_dto.TokenResponseDto;
import jakarta.security.auth.message.AuthException;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {
   // UserDto authenticate(String email, String password);
    TokenResponseDto getAccessToken(String refreshToken);

    TokenResponseDto login(LoginRequestDto loginRequest) throws AuthException;
}