package com.example.end.dto;

import com.example.end.models.Procedure;
import jakarta.validation.constraints.*;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcedureDto implements Serializable {
    private static final long serialVersionUID = 1L;

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
    private Double price;

    @Schema(description = "Description of the procedure")
    private String description;

    @NotNull(message = "Category ID cannot be null")
    @Schema(description = "ID of the category this procedure belongs to", example = "1")
    private Long categoryId;

    @Schema(description = "Is the procedure active")
    private Boolean isActive;
}
