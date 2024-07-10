package com.example.end.controller;

import com.example.end.controller.api.CategoryApi;
import com.example.end.dto.CategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.exceptions.CategoryNotFoundException;
import com.example.end.models.Category;
import com.example.end.service.interfaces.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class CategoryController implements CategoryApi {

    private final CategoryService categoryService;

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }
    @Override
    public CategoryDto getCategoryById(Long id) {
        return categoryService.getCategoryById(id);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto updatedCategoryDto) {
        return categoryService.updateCategory(id,updatedCategoryDto);
    }
   @Override
   public CategoryDto deleteCategory(Long id) {
    return categoryService.deleteCategory(id);
}
}

