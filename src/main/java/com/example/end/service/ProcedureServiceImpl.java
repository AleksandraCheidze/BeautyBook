package com.example.end.service;

import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.infrastructure.exceptions.ResourceAlreadyExistsException;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
import com.example.end.mapping.ProcedureMapper;
import com.example.end.models.Category;
import com.example.end.models.Procedure;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.ProcedureRepository;
import com.example.end.service.interfaces.ProcedureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the ProcedureService interface.
 * Provides business logic for managing procedures, including creation, update, deletion, and retrieval.
 */
@RequiredArgsConstructor
@Service
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final ProcedureMapper procedureMapper;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new procedure.
     *
     * @param newProcedureDto the DTO containing the details of the procedure to create
     * @return the created ProcedureDto
     */

    @Override
    public ProcedureDto createProcedure(NewProcedureDto newProcedureDto) {
        if (procedureRepository.existsByName(newProcedureDto.getName())) {
            throw new ResourceAlreadyExistsException(
                    "Procedure with name '" + newProcedureDto.getName() + "' already exists. Please choose a different name.");
        }

        Category category = categoryRepository.findById(newProcedureDto.getCategoryId())
                .orElseThrow(() -> {
                    throw new ResourceNotFoundException(
                            "Category with ID " + newProcedureDto.getCategoryId() + " not found");
                });

        Procedure procedure = procedureMapper.fromNewProcedureDto(newProcedureDto);
        procedure.setCategory(category);
        Procedure savedProcedure = procedureRepository.save(procedure);

        return procedureMapper.toDto(savedProcedure);
    }

    /**
     * Updates an existing procedure.
     *
     * @param updatedProcedureDto the DTO containing the updated procedure details
     * @throws  if the procedure with the given ID does not exist
     */

    @Override
    public ProcedureDto update(Long id, ProcedureDto updatedProcedureDto) {
        Procedure existingProcedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException ("Procedure not found with id: " + id));

        Procedure updatedProcedure = procedureMapper.fromProcedureDto(updatedProcedureDto);
        updatedProcedure.setId(existingProcedure.getId());

        Procedure savedProcedure = procedureRepository.save(updatedProcedure);
        return procedureMapper.toDto(savedProcedure);
    }
    /**
     * Deletes a procedure by its ID.
     *
     * @param id the ID of the procedure to delete
     * @return the deleted ProcedureDto
     * @throws  if the procedure with the given ID does not exist
     */
    @Override
    public ProcedureDto deleteById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException ("Procedure with ID " + id + " was not found"));
        procedureRepository.deleteById(id);
        return procedureMapper.toDto(procedure);
    }

    /**
     * Retrieves all procedures.
     *
     * @return a list of ProcedureDto objects representing all procedures
     */
    @Override
    public List<ProcedureDto> findAll() {
        List<Procedure> procedures = procedureRepository.findAll();
        return procedures.stream()
                .map(procedureMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a procedure by its ID.
     *
     * @param id the ID of the procedure to retrieve
     * @return the ProcedureDto representing the procedure with the given ID
     * @throws  if the procedure with the given ID does not exist
     */
    @Override
    public ProcedureDto findById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Procedure with ID " + id + " was not found"));
        return procedureMapper.toDto(procedure);
    }

    /**
     * Retrieves all procedures for a specific category.
     *
     * @param categoryId the ID of the category to retrieve procedures for
     * @return a list of ProcedureByCategoryDto representing the procedures for the specified category
     * @throws  if no procedures are found for the given category ID
     */
    @Override
    public List<ProcedureByCategoryDto> findProceduresByCategoryId(Long categoryId) {
        List<Procedure> procedures = procedureRepository.findProceduresByCategoryId(categoryId);
        if (procedures.isEmpty()) {
            throw new ResourceNotFoundException("Procedures for category with ID " + categoryId + " not found");
        }
        return procedures.stream()
                .map(procedureMapper::procedureByCategoryToDto)
                .collect(Collectors.toList());
    }
}