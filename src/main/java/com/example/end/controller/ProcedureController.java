package com.example.end.controller;


import com.example.end.controller.api.ProcedureApi;
import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.service.interfaces.ProcedureService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RequiredArgsConstructor
@RestController
public class ProcedureController implements ProcedureApi {

  private final ProcedureService procedureService;
  @Override
  public ProcedureDto createProcedure(NewProcedureDto newProcedureDto) {
    return procedureService.createProcedure(newProcedureDto);
  }
  @Override
  public void update(ProcedureDto procedure) {
  procedureService.update(procedure);
  }
  @Override
  public ProcedureDto deleteById(Long id) {
    return procedureService.deleteById(id);
  }

  @Override
  public List<ProcedureDto> findAll() {
  return procedureService.findAll();
  }

  @Override
  public ProcedureDto findById(Long id) {
    return procedureService.findById(id);
  }

  @Override
  public List<ProcedureByCategoryDto> findProceduresByCategoryId(Long categoryId) {
    return procedureService.findProceduresByCategoryId(categoryId);
  }
}



