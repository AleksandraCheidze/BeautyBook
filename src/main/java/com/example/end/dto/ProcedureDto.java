package com.example.end.dto;

import com.example.end.models.Procedure;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProcedureDto {

    @Schema(description = "Unique identifier of the procedure", example = "1")
    private Long id;

    @NotBlank(message = "Procedure name cannot be blank")
    @Pattern(regexp = "^[A-Z][a-zA-Z0-9\\s]*$", message = "Procedure name must start with an uppercase letter and contain only alphanumeric characters")
    @Size(min = 3, message = "Procedure name must contain at least 3 characters")
    @Schema(description = "Name of the procedure", example = "Men haircut")
    private String name;

    @Min(value = 1, message = "Price must be at least 1")
    @Max(value = 500, message = "Price must be at most 500")
    @NotNull(message = "Price cannot be null")
    @Schema(description = "Price of the procedure", example = "50.0")
    private double price;

}

