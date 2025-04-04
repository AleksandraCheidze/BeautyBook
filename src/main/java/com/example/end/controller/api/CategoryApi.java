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

        @Operation(summary = "Get All Categories (Public)",
                description = "Available to all users")
        @GetMapping
        List<CategoryDto> getAllCategories();

        @Operation(summary = "Get Category by ID (Public)",
                description = "Available to all users")
        @GetMapping("/{id}")
        CategoryDto getCategoryById
                (@Parameter(description = "category identifier", example = "1")
                 @PathVariable("id") Long id);

        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Create Category (ADMIN)",
                description = "Available to ADMIN")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201",
                        description = "Category created successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CategoryDto.class))),
                @ApiResponse(responseCode = "400",
                        description = "Invalid category data",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = ValidationErrorsDto.class))),
                @ApiResponse(responseCode = "409",
                        description = "Category with this name already exists",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto);


        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Update Category (ADMIN)",
                description = "Available to ADMIN")
        @PutMapping("/{id}")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200",
                        description = "Category updated successfully",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = CategoryDto.class))),
                @ApiResponse(responseCode = "404",
                        description = "Category not found")
        })
        CategoryDto updateCategory(@PathVariable("id") Long id,
                                   @RequestBody @Valid CategoryDto categoryDto);

        @PreAuthorize("hasRole('ADMIN')")
        @Operation(summary = "Delete Category (ADMIN)",
                description = "Delete category and all associated procedures")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204",
                        description = "Category and all its procedures deleted successfully"),
                @ApiResponse(responseCode = "404",
                        description = "Category not found",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = StandardResponseDto.class)))
        })
        @DeleteMapping("/{id}")
        @ResponseStatus(HttpStatus.NO_CONTENT)
        void deleteCategory(@PathVariable("id") Long id);

}
