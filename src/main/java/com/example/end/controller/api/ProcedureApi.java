package com.example.end.controller.api;

import com.example.end.dto.NewProcedureDto;
import com.example.end.dto.ProcedureByCategoryDto;
import com.example.end.dto.ProcedureDto;
import com.example.end.dto.StandardResponseDto;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RequestMapping("/api/procedures")
@Tags(value = {
        @Tag(name = "Procedures", description = "Procedure management endpoints")
})
@ApiResponses(value = {
        @ApiResponse(responseCode = "401",
                description = "User not authenticated",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = StandardResponseDto.class))),
        @ApiResponse(responseCode = "403",
                description = "Forbidden",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = StandardResponseDto.class)))
})
public interface ProcedureApi {

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Create a procedure.Available to ADMIN", description = "Available to ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Procedure created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcedureDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorsDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProcedureDto createProcedure(@RequestBody @Valid NewProcedureDto newProcedureDto);

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Update a procedure.Available to ADMIN", description = "Available to ADMIN")
    @PutMapping
    void update(@RequestBody @Valid ProcedureDto procedureDto);

    @PreAuthorize("hasAuthority('ADMIN')")
    @Operation(summary = "Delete a procedure by ID.Available to ADMIN", description = "Available to ADMIN")
    @DeleteMapping("/{id}")
    ProcedureDto deleteById(@Parameter(description = "procedure id", example = "1")
                            @PathVariable("id") Long id);



    @Operation(summary = "Get a list of all procedures.Available to all users", description = "Retrieve all procedures.Available to all users")
    @GetMapping()
    List<ProcedureDto> findAll();

    @Operation(summary = "Find a procedure by ID.Available to all users", description = "Retrieve a procedure by its ID.Available to all users")
    @GetMapping("/{id}")
    ProcedureDto findById(@Parameter(description = "procedure id", example = "1")
            @PathVariable("id") Long id);

    @Operation(summary = "Find procedures by category ID.Available to all users", description = "Retrieve procedures by category ID.Available to all users")
    @GetMapping("/by-category/{categoryId}")
    List<ProcedureByCategoryDto> findProceduresByCategoryId(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable("categoryId") Long categoryId);
}