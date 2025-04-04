package com.example.end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewUserDetailsDto {
    @Schema(description = "Description of the user", example = "I am a manicure master with 10 years of experience")
    @Size(max = 255, message = "Description cannot be longer than 255 characters")
    private String description;

    @NotBlank(message = "Phone number cannot be empty")
    @Schema(description = "Phone number of the user", example = "+4917644545615")
    @Pattern(
            regexp = "^\\+?[1-9]\\d{1,14}$",
            message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Address cannot be blank")
    @Schema(description = "Address of the user", example = "Berlin, Salvador straße 88, 13446")
    @Size(max = 50, message = "Address cannot be longer than 50 characters")
    private String address;

    @NotEmpty(message = "List of categories cannot be blank")
    @Schema(description = "List of category IDs to be added to the user")
    private List<Long> categoryIds;

    @NotEmpty(message = "Procedure IDs cannot be empty")
    @Schema(description = "List of Category procedures IDs to be added to the user")
    private List<Long> procedureIds;
}
