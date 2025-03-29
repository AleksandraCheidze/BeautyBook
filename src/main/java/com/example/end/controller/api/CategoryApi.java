package com.example.end.controller.api;

import com.example.end.dto.CategoryDto;
import com.example.end.dto.StandardResponseDto;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Categories", description = "API endpoints for category management")
@RequestMapping("/api/categories")
@Tags(value = {
                @Tag(name = "Categories", description = "Category management endpoints")
})
@ApiResponses(value = {
                @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))),
                @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
})
public interface CategoryApi {
        @Operation(summary = "Get All Categories (Public)", description = "Get all categories in the system. Access: All users")
        @GetMapping
        List<CategoryDto> getAllCategories();

        @Operation(summary = "Get Category by ID (Public)", description = "Get a specific category by its ID. Access: All users")
        @GetMapping("/{id}")
        CategoryDto getCategoryById(
                        @Parameter(description = "Category identifier", example = "1", required = true) @PathVariable("id") Long id);

        @Operation(summary = "Create Category (ADMIN)", description = "Create a new category in the system. Access: ADMIN only")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Category created successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryDto.class))),
                        @ApiResponse(responseCode = "400", description = "Invalid category data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))),
                        @ApiResponse(responseCode = "401", description = "User not authenticated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class))),
                        @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        @SecurityRequirement(name = "bearerAuth")
        CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto);

        @Operation(summary = "Update Category (ADMIN)", description = "Update an existing category. Access: ADMIN only")
        @PutMapping("/{id}")
        @SecurityRequirement(name = "bearerAuth")
        CategoryDto updateCategory(
                        @Parameter(description = "Category identifier", example = "1", required = true) @PathVariable("id") Long id,
                        @RequestBody @Valid CategoryDto categoryDto);

        @Operation(summary = "Delete Category (ADMIN)", description = "Delete a category from the system. Access: ADMIN only")
        @DeleteMapping("/{id}")
        @SecurityRequirement(name = "bearerAuth")
        CategoryDto deleteCategory(
                        @Parameter(description = "Category identifier", example = "1", required = true) @PathVariable("id") Long id);
}