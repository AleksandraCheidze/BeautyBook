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
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/categories")
@Tags(value = {
        @Tag(name = "Categories", description = "Category management endpoints")
})
@ApiResponses(value = {
        @ApiResponse(responseCode = "401",
                description = "User not authenticated",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = StandardResponseDto.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = StandardResponseDto.class)))
})
public interface CategoryApi {
    @Operation(summary = "Get all categories.Available to all users", description = "Available to all users")
    @GetMapping
    List<CategoryDto> getAllCategories();

    @Operation(summary = "Get category by ID.Available to all users", description = "Available to all users")
    @GetMapping("/{id}")
    CategoryDto getCategoryById(@Parameter(description = "category identifier", example = "1")
                                @PathVariable("id") Long id);
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a category.Available to ADMIN", description = "Available to ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Category created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid category data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorsDto.class)))
    })

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto);
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a category.Available to ADMIN", description = "Available to ADMIN")
    @PutMapping("/{id}")
    CategoryDto updateCategory(@PathVariable ("id") Long id, @RequestBody @Valid CategoryDto categoryDto);
    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a category.Available to ADMIN", description = "Available to ADMIN")
    @DeleteMapping("/{id}")
    CategoryDto deleteCategory(@PathVariable ("id") Long id);
}