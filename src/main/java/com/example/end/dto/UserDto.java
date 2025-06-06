package com.example.end.dto;

import com.example.end.models.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing a user")
public class UserDto {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique identifier of the user", example = "1")
    private Long id;

    @Pattern(regexp = "[A-Z][a-z]{3,}")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @Pattern(regexp = "[A-Z][a-z]{3,}")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Set of roles assigned to the user")
    @NotNull(message = "Role cannot be null")
    private User.Role role;

}
