package com.example.end.controller;

import com.example.end.controller.api.CategoryApi;
import com.example.end.dto.CategoryDto;
import com.example.end.service.interfaces.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for managing categories.
 * Provides endpoints for creating, updating, deleting, and retrieving categories.
 */
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "API endpoints for category management")
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    @GetMapping
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get All Categories (Public)", description = "Get all categories in the system. Access: All users")
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
    @Override
    @GetMapping("/{id}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get Category by ID (Public)", description = "Get a specific category by its ID. Access: All users")
    public CategoryDto getCategoryById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }
    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Category (ADMIN)", description = "Create a new category in the system. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }
    @Override
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Category (ADMIN)", description = "Update an existing category in the system. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public CategoryDto updateCategory(
            @PathVariable("id") Long id,
            @RequestBody @Valid CategoryDto categoryDto) {
        return categoryService.updateCategory(id, categoryDto);
    }

    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete Category (ADMIN)", description = "Delete a category from the system. Access: ADMIN only")
    @SecurityRequirement(name = "bearerAuth")
    public CategoryDto deleteCategory(@PathVariable("id") Long id) {
        return categoryService.deleteCategory(id);
    }
}
