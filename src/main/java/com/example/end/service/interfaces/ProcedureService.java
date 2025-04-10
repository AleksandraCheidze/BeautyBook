package com.example.end.service.interfaces;

import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface ProcedureService {


  ProcedureDto createProcedure(NewProcedureDto procedureDto);

  public ProcedureDto update(Long id, ProcedureDto updatedProcedureDto);

  ProcedureDto deleteById(Long id);

  List<ProcedureDto> findAll();

  ProcedureDto findById(Long id);

  List<ProcedureByCategoryDto> findProceduresByCategoryId(Long categoryId);
}