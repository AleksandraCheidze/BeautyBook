package com.example.end.dto;


import com.example.end.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewUserDto {

    @NotNull
    @Schema(description = "Users firstname", example = "Joe")
    private String firstName;

    @NotNull
    @Schema(description = "Users lastname", example = "Doe")
    private String lastName;

    @Email
    @NotNull
    @Schema(description = "Email пользователя", example = "user@mail.com")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$")
    @Schema(description = "Users password", example = "Qwerty007!")
    private String hashPassword;

    @NotNull
    @Schema(description = "Users role", example = "Master")
    private User.Role role;

}

