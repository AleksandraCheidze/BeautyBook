package com.example.end.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequestDto {


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(example = "alexa.cx@gmx.de")
    private String email;


    @NotBlank(message = "Password is required")
    @Schema(example = "Qwerty007!")
    private String password;
}
