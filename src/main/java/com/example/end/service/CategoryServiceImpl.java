package com.example.end.service;

import com.example.end.dto.CategoryDto;
import com.example.end.exceptions.CategoryNotFoundException;
import com.example.end.mapping.CategoryMapper;
import com.example.end.models.Category;
import com.example.end.repository.CategoryRepository;
import com.example.end.service.interfaces.CategoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    @Override
    public List<CategoryDto> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new CategoryNotFoundException("Die Liste der Kategorien ist leer");
        }
        return categories.stream()
                .map(categoryMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
       new CategoryNotFoundException("Die Kategorie wurde nicht gefunden: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto updatedCategoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Die Kategorie wurde nicht gefunden mit ID: " + id));

        Category updatedCategory = categoryMapper.toEntity(updatedCategoryDto);
        updatedCategory.setId(existingCategory.getId());

        Category savedCategory = categoryRepository.save(updatedCategory);
        return categoryMapper.toDto(savedCategory);
    }

    @Override
    public CategoryDto deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Die Kategorie mit der ID " + id + " wurde nicht gefunden"));
            categoryRepository.deleteById(id);
            return categoryMapper.toDto(category);
    }
}