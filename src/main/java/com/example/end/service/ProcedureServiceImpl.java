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
@RequiredArgsConstructor
@Service
public class ProcedureServiceImpl implements ProcedureService {

    private final ProcedureRepository procedureRepository;
    private final ProcedureMapper procedureMapper;

    @Override
    public ProcedureDto createProcedure(NewProcedureDto newProcedureDto) {
        Procedure procedure = procedureMapper.toEntity(newProcedureDto);
        Procedure createdProcedure = procedureRepository.save(procedure);
        return procedureMapper.toDto(createdProcedure);
    }

    @Override
    public void update(ProcedureDto procedure) {
        if (!procedureRepository.existsById(procedure.getId())) {
            throw new ProcedureNotFoundException("Dienstleistung mit dieser ID  wurde nicht gefunden");
        }
            Procedure entity = procedureMapper.toEntity(procedure);
        procedureRepository.save(entity);
        }

    @Override
    public ProcedureDto deleteById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ProcedureNotFoundException("Dienstleistung mit der ID " + id + " wurde nicht gefunden"));
        procedureRepository.deleteById(id);
        return procedureMapper.toDto(procedure);
    }


    @Override
    public List<ProcedureDto> findAll() {
        List<Procedure> procedures = procedureRepository.findAll();
        return procedures.stream()
                .map(procedureMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProcedureDto findById(Long id) {
        Procedure procedure = procedureRepository.findById(id)
                .orElseThrow(() -> new ProcedureNotFoundException("Dienstleistung mit der ID " + id + " wurde nicht gefunden"));
        return procedureMapper.toDto(procedure);
    }
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
