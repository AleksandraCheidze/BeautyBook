package com.example.end.service;

import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.exceptions.CategoryNotFoundException;
import com.example.end.exceptions.ProcedureNotFoundException;
import com.example.end.mapping.ProcedureMapper;
import com.example.end.models.Procedure;
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

    /**
     * Creates a new procedure.
     *
     * @param newProcedureDto the DTO containing the details of the procedure to create.
     * @return the created ProcedureDto.
     */
    @Override
    public ProcedureDto createProcedure(NewProcedureDto newProcedureDto) {
        Procedure procedure = procedureMapper.fromNewProcedureDto(newProcedureDto);
        Procedure createdProcedure = procedureRepository.save(procedure);
        return procedureMapper.toDto(createdProcedure);
    }

    /**
     * Updates an existing procedure.
     *
     * @param procedure the DTO containing the updated procedure details.
     * @throws ProcedureNotFoundException if the procedure with the given ID does not exist.
     */
    @Override
    public void update(ProcedureDto procedure) {
        if (!procedureRepository.existsById(procedure.getId())) {
            throw new ProcedureNotFoundException("Dienstleistung mit dieser ID wurde nicht gefunden");
        }
        Procedure entity = procedureMapper.fromProcedureDto(procedure);
        procedureRepository.save(entity);
    }

    /**
     * Deletes a procedure by its ID.
     *
     * @param id the ID of the procedure to delete.
     * @return the deleted ProcedureDto.
     * @throws ProcedureNotFoundException if the procedure with the given ID does not exist.
     */
    @Override
    public ProcedureDto deleteById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ProcedureNotFoundException("Dienstleistung mit der ID " + id + " wurde nicht gefunden"));
        procedureRepository.deleteById(id);
        return procedureMapper.toDto(procedure);
    }

    /**
     * Retrieves all procedures.
     *
     * @return a list of ProcedureDto objects representing all procedures.
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
     * @param id the ID of the procedure to retrieve.
     * @return the ProcedureDto representing the procedure with the given ID.
     * @throws ProcedureNotFoundException if the procedure with the given ID does not exist.
     */
    @Override
    public ProcedureDto findById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ProcedureNotFoundException("Dienstleistung mit der ID " + id + " wurde nicht gefunden"));
        return procedureMapper.toDto(procedure);
    }

    /**
     * Retrieves all procedures for a specific category.
     *
     * @param categoryId the ID of the category to retrieve procedures for.
     * @return a list of ProcedureByCategoryDto representing the procedures for the specified category.
     * @throws CategoryNotFoundException if no procedures are found for the given category ID.
     */
    @Override
    public List<ProcedureByCategoryDto> findProceduresByCategoryId(Long categoryId) {
        List<Procedure> procedures = procedureRepository.findProceduresByCategoryId(categoryId);
        if (procedures.isEmpty()) {
            throw new CategoryNotFoundException("Procedures for category with ID " + categoryId + " not found");
        }
        return procedures.stream()
                .map(procedureMapper::procedureByCategoryToDto)
                .collect(Collectors.toList());
    }
}
