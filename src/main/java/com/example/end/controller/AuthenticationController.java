package com.example.end.controller;

import com.example.end.dto.LoginRequestDto;
import com.example.end.infrastructure.security.sec_dto.RefreshRequestDto;
import com.example.end.infrastructure.security.sec_dto.TokenResponseDto;
import com.example.end.service.interfaces.AuthenticationService;
import com.example.end.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final UserService userService;

    private final AuthenticationService authenticationService;
    @Operation(summary = "Authenticate user", description = "Authenticate a user with the provided email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful",
                    content = @Content(schema = @Schema(implementation = TokenResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestBody @Valid LoginRequestDto loginRequest, HttpServletResponse response) throws AuthException {
        TokenResponseDto tokenDto = authenticationService.login(loginRequest);
        Cookie cookie = new Cookie("Access-Token", tokenDto.getAccessToken());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(tokenDto);
    }

    @Operation(summary = "Get new access token", description = "Get a new access token using a refresh token.")
    @PostMapping("/access")
    public ResponseEntity<TokenResponseDto> getNewAccessToken(@RequestBody RefreshRequestDto request) {
        TokenResponseDto accessToken = authenticationService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(accessToken);
    }


    @Operation(summary = "Logout", description = "Logout a user by invalidating the access token cookie.")
    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("Access-Token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

}

