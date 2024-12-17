package com.example.end.service;

import com.example.end.dto.CategoryDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.exceptions.CategoryNotFoundException;
import com.example.end.exceptions.ProcedureNotFoundException;
import com.example.end.mapping.CategoryMapper;
import com.example.end.mapping.ProcedureMapper;
import com.example.end.models.Category;
import com.example.end.models.Procedure;
import com.example.end.repository.CategoryRepository;
import com.example.end.service.interfaces.CategoryService;
import com.example.end.service.interfaces.ProcedureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the CategoryService interface.
 * Provides business logic for managing categories and their associated procedures.
 */
@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProcedureService procedureService;
    private final ProcedureMapper procedureMapper;

    /**
     * Retrieves all categories from the database.
     *
     * @return a list of CategoryDto objects representing all categories.
     * @throws CategoryNotFoundException if no categories are found.
     */
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

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category to retrieve.
     * @return the CategoryDto representing the category.
     * @throws CategoryNotFoundException if the category with the given ID does not exist.
     */
    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new CategoryNotFoundException("Die Kategorie wurde nicht gefunden: " + id));
        return categoryMapper.toDto(category);
    }

    /**
     * Retrieves procedures for the specified categories and filters them by procedure IDs.
     *
     * @param selectedCategories the set of categories to fetch procedures from.
     * @param procedureIds the list of procedure IDs to filter by.
     * @return a set of Procedure entities matching the criteria.
     * @throws ProcedureNotFoundException if a procedure ID is not found.
     */
    @Override
    public Set<Procedure> getProceduresForCategories(Set<Category> selectedCategories, List<Long> procedureIds) {
        Set<Procedure> selectedProcedures = new HashSet<>();

        for (Category category : selectedCategories) {
            List<ProcedureByCategoryDto> procedureDtosForCategory = procedureService.findProceduresByCategoryId(category.getId());

            List<Procedure> proceduresForCategory = procedureDtosForCategory.stream()
                    .map(procedureMapper::toEntity)
                    .collect(Collectors.toList());

            for (Long procedureId : procedureIds) {
                Procedure procedure = proceduresForCategory.stream()
                        .filter(p -> p.getId().equals(procedureId))
                        .findFirst()
                        .orElseThrow(() -> new ProcedureNotFoundException("Procedure not found for id: " + procedureId));

                selectedProcedures.add(procedure);
            }
        }

        return selectedProcedures;
    }

    /**
     * Creates a new category based on the provided CategoryDto.
     *
     * @param categoryDto the CategoryDto containing the details of the category to create.
     * @return the created CategoryDto.
     */
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        Category category = categoryMapper.toEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDto(savedCategory);
    }

    /**
     * Updates an existing category based on the provided category ID and updated CategoryDto.
     *
     * @param id the ID of the category to update.
     * @param updatedCategoryDto the CategoryDto containing the updated category data.
     * @return the updated CategoryDto.
     * @throws CategoryNotFoundException if the category with the given ID does not exist.
     */
    @Override
    public CategoryDto updateCategory(Long id, CategoryDto updatedCategoryDto) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Die Kategorie wurde nicht gefunden mit ID: " + id));

        Category updatedCategory = categoryMapper.toEntity(updatedCategoryDto);
        updatedCategory.setId(existingCategory.getId());

        Category savedCategory = categoryRepository.save(updatedCategory);
        return categoryMapper.toDto(savedCategory);
    }

    /**
     * Deletes a category based on the provided category ID.
     *
     * @param id the ID of the category to delete.
     * @return the deleted CategoryDto.
     * @throws CategoryNotFoundException if the category with the given ID does not exist.
     */
    @Override
    public CategoryDto deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Die Kategorie mit der ID " + id + " wurde nicht gefunden"));
        categoryRepository.deleteById(id);
        return categoryMapper.toDto(category);
    }
}
