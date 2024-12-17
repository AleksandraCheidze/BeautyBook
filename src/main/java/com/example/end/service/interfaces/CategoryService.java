package com.example.end.service.interfaces;

import com.example.end.dto.CategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.models.Category;
import com.example.end.models.Procedure;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Service
public interface CategoryService {

    List<CategoryDto> getAllCategories();

    CategoryDto getCategoryById(Long id);

    Set<Procedure> getProceduresForCategories(Set<Category> selectedCategories, List<Long> procedureIds);

    CategoryDto createCategory(CategoryDto categoryDto);


    CategoryDto updateCategory(Long id, CategoryDto updatedCategoryDto);

    CategoryDto deleteCategory(Long id);
}
