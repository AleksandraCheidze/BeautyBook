package com.example.end.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Unique identifier of the category", example = "1")
    private Long id;

    @NotBlank(message = "Category name cannot be blank")
    @Size(min = 3, message = "Category name must contain at least 3 characters")
    @Schema(description = "Name of the category", example = "Hairstylist")
    private String name;
}
