package com.example.end.controller;

import com.example.end.controller.api.ProcedureApi;
import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.service.interfaces.ProcedureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing procedures.
 * Provides endpoints for creating, updating, deleting, and retrieving procedures.
 */
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/procedures")
@Tag(name = "Procedures", description = "API endpoints for procedure management")
public class ProcedureController implements ProcedureApi {

  private final ProcedureService procedureService;

  @Override
  @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Create Procedure (ADMIN)", description = "Create a new procedure in the system. Access: ADMIN only")
  @SecurityRequirement(name = "bearerAuth")
    public ProcedureDto createProcedure(@RequestBody @Valid NewProcedureDto newProcedureDto) {
        return procedureService.createProcedure(newProcedureDto);
    }

  @Override
    @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Update Procedure (ADMIN)", description = "Update an existing procedure in the system. Access: ADMIN only")
  @SecurityRequirement(name = "bearerAuth")
    public ProcedureDto updateProcedure(
            @PathVariable("id") Long id,
            @RequestBody @Valid ProcedureDto procedureDto) {
        return procedureService.updateProcedure(id, procedureDto);
    }

  @Override
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Operation(summary = "Delete Procedure (ADMIN)", description = "Delete a procedure from the system. Access: ADMIN only")
  @SecurityRequirement(name = "bearerAuth")
    public ProcedureDto deleteById(@PathVariable("id") Long id) {
        return procedureService.deleteById(id);
    }

  @Override
  @GetMapping
  @PreAuthorize("permitAll()")
  @Operation(summary = "Get All Procedures (Public)", description = "Get all procedures in the system. Access: All users")
    public List<ProcedureDto> findAll() {
        return procedureService.findAll();
  }

  @Override
  @GetMapping("/{id}")
  @PreAuthorize("permitAll()")
  @Operation(summary = "Get Procedure by ID (Public)", description = "Get a specific procedure by its ID. Access: All users")
    public ProcedureDto findById(@PathVariable("id") Long id) {
        return procedureService.findById(id);
    }

  @Override
  @GetMapping("/by-category/{categoryId}")
  @PreAuthorize("permitAll()")
  @Operation(summary = "Get Procedures by Category (Public)", description = "Get all procedures for a specific category. Access: All users")
    public List<ProcedureByCategoryDto> findProceduresByCategoryId(@PathVariable("categoryId") Long categoryId) {
        return procedureService.findProceduresByCategoryId(categoryId);
  }
}
