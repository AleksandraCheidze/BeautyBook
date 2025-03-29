package com.example.end.service;

import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.infrastructure.exceptions.CategoryNotFoundException;
import com.example.end.infrastructure.exceptions.ProcedureAlreadyExistsException;
import com.example.end.infrastructure.exceptions.ProcedureNotFoundException;
import com.example.end.mapping.ProcedureMapper;
import com.example.end.models.Category;
import com.example.end.models.Procedure;
import com.example.end.repository.CategoryRepository;
import com.example.end.repository.ProcedureRepository;
import com.example.end.service.interfaces.ProcedureService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final CategoryRepository categoryRepository;
    private final ProcedureMapper procedureMapper;

    @Override
    @Transactional
    @CacheEvict(value = { "allProcedures", "proceduresByCategory" }, allEntries = true)
    public ProcedureDto createProcedure(NewProcedureDto newProcedureDto) {
        log.debug("Creating new procedure: {}", newProcedureDto.getName());

        if (procedureRepository.existsByName(newProcedureDto.getName())) {
            log.warn("Procedure with name '{}' already exists", newProcedureDto.getName());
            throw new ProcedureAlreadyExistsException(
                    "Procedure with name '" + newProcedureDto.getName() + "' already exists. Please choose a different name.");
        }

        Category category = categoryRepository.findById(newProcedureDto.getCategoryId())
                .orElseThrow(() -> {
                    log.warn("Category with ID {} not found", newProcedureDto.getCategoryId());
                    return new IllegalArgumentException(
                            "Category with ID " + newProcedureDto.getCategoryId() + " not found");
                });

        Procedure procedure = procedureMapper.fromNewProcedureDto(newProcedureDto);
        procedure.setCategory(category);
        procedure.setActive(true);
        procedure.setDescription("");
        Procedure savedProcedure = procedureRepository.save(procedure);
        log.info("Procedure created with ID: {}", savedProcedure.getId());

        return procedureMapper.toDto(savedProcedure);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "procedure", key = "#procedure.id"),
            @CacheEvict(value = { "allProcedures", "proceduresByCategory" }, allEntries = true)
    })
    public ProcedureDto update(ProcedureDto procedure) {
        log.debug("Updating procedure with ID: {}", procedure.getId());

        if (!procedureRepository.existsById(procedure.getId())) {
            log.warn("Procedure with ID {} not found for update", procedure.getId());
            throw new ProcedureNotFoundException("Procedure with ID " + procedure.getId() + " not found");
        }

        Procedure entity = procedureMapper.fromProcedureDto(procedure);

        if (procedure.getCategoryId() != null) {
            Category category = categoryRepository.findById(procedure.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Category with ID {} not found", procedure.getCategoryId());
                        return new IllegalArgumentException(
                                "Category with ID " + procedure.getCategoryId() + " not found");
                    });
            entity.setCategory(category);
        }

        entity.setDescription(procedure.getDescription());
        entity.setActive(procedure.getIsActive());

        Procedure updatedProcedure = procedureRepository.save(entity);
        log.info("Procedure with ID {} updated successfully", procedure.getId());
        return procedureMapper.toDto(updatedProcedure);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "procedure", key = "#id"),
            @CacheEvict(value = { "allProcedures", "proceduresByCategory" }, allEntries = true)
    })
    public ProcedureDto deleteById(Long id) {
        log.debug("Deleting procedure with ID: {}", id);
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Procedure with ID {} not found for deletion", id);
                    return new ProcedureNotFoundException("Procedure with ID " + id + " not found");
                });

        procedureRepository.deleteById(id);
        log.info("Procedure with ID {} deleted successfully", id);

        return procedureMapper.toDto(procedure);
    }

    @Override
    @Cacheable(value = "allProcedures")
    public List<ProcedureDto> findAll() {
        log.debug("Retrieving all procedures");

        List<Procedure> procedures = procedureRepository.findAll();
        log.info("Retrieved {} procedures", procedures.size());

        return procedures.stream()
                .map(procedureMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "procedure", key = "#id")
    public ProcedureDto findById(Long id) {
        log.debug("Finding procedure with ID: {}", id);

        Procedure procedure = getProcedureById(id);

        if (procedure == null) {
            log.error("Procedure with ID: {} not found", id);
            throw new EntityNotFoundException("Procedure not found with ID: " + id);
        }
        log.info("Found procedure with ID: {}", id);
        return procedureMapper.toDto(procedure);
    }

    @Override
    @Cacheable(value = "proceduresByCategory", key = "#categoryId")
    public List<ProcedureByCategoryDto> findProceduresByCategoryId(Long categoryId) {
        log.debug("Finding procedures for category ID: {}", categoryId);

        List<Procedure> procedures = procedureRepository.findProceduresByCategoryId(categoryId);

        if (procedures.isEmpty()) {
            log.warn("No procedures found for category ID: {}", categoryId);
            throw new CategoryNotFoundException("Procedures for category with ID " + categoryId + " not found");
        }

        log.info("Found {} procedures for category ID: {}", procedures.size(), categoryId);

        return procedures.stream()
                .map(procedureMapper::procedureByCategoryToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @CachePut(value = "procedure", key = "#id")
    @CacheEvict(value = { "allProcedures", "proceduresByCategory" }, allEntries = true)
    public ProcedureDto updateProcedure(Long id, ProcedureDto procedureDto) {
        log.debug("Updating procedure with ID: {} using procedureDto", id);

        Procedure procedure = getProcedureById(id);

        if (!procedure.getName().equals(procedureDto.getName()) &&
                procedureRepository.existsByName(procedureDto.getName())) {
            log.warn("Cannot update procedure with ID {} - name '{}' is already taken",
                    id, procedureDto.getName());
            throw new ProcedureAlreadyExistsException(
                    "Procedure with name '" + procedureDto.getName() +
                    "' already exists. Please choose a different name.");
        }

        procedure.setName(procedureDto.getName());
        procedure.setDescription(procedureDto.getDescription());
        procedure.setPrice(procedureDto.getPrice());
        procedure.setActive(procedureDto.getIsActive());

        if (procedureDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(procedureDto.getCategoryId())
                    .orElseThrow(() -> {
                        log.warn("Category with ID {} not found", procedureDto.getCategoryId());
                        return new IllegalArgumentException(
                                "Category with ID " + procedureDto.getCategoryId() + " not found");
                    });
            procedure.setCategory(category);
        }

        Procedure updatedProcedure = procedureRepository.save(procedure);
        log.info("Procedure with ID {} updated successfully", id);

        return procedureMapper.toDto(updatedProcedure);
    }

    private Procedure getProcedureById(Long id) {
        return procedureRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Procedure with ID {} not found", id);
                    return new ProcedureNotFoundException("Procedure with ID " + id + " not found");
                });
    }
}
