package com.example.end.service.interfaces;

import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.models.Procedure;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public interface ProcedureService {

  ProcedureDto createProcedure(NewProcedureDto procedureDto);

  ProcedureDto update(ProcedureDto procedure);

  ProcedureDto deleteById(Long id);

  List<ProcedureDto> findAll();

  ProcedureDto findById(Long id);

  List<ProcedureByCategoryDto> findProceduresByCategoryId(Long categoryId);

  @Transactional
  @CachePut(value = "procedure", key = "#id")
  @CacheEvict(value = { "allProcedures", "proceduresByCategory" }, allEntries = true)
  ProcedureDto updateProcedure(Long id, ProcedureDto procedureDto);
}