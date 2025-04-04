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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/procedures")
@Tags(value = {
        @Tag(name = "Procedures", description = "API endpoints for procedure management")
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

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create Procedure (ADMIN)",
            description = "Available to ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Procedure created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProcedureDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorsDto.class))),
            @ApiResponse(responseCode = "409",
                    description = "Procedure with this name already exists in the specified category",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StandardResponseDto.class)))
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ProcedureDto createProcedure(@RequestBody @Valid NewProcedureDto newProcedureDto);


    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update Procedure (ADMIN)",
            description = "Available to ADMIN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Procedure updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProcedureDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class))),
            @ApiResponse(responseCode = "404", description = "Procedure not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardResponseDto.class)))
    })
    @PutMapping("/{id}")
    ProcedureDto update(@PathVariable ("id") Long id,
            @RequestBody @Valid ProcedureDto updatedProcedureDto);

    @Operation(summary = "Delete Procedure (ADMIN)", description = "Delete a procedure from the system. Access: ADMIN only")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Procedure deleted"),
            @ApiResponse(responseCode = "404", description = "Procedure not found"),
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteById(@Parameter(description = "Procedure ID", example = "1")
                    @PathVariable("id") Long id);

    @Operation(summary = "Get All Procedures (Public)",
            description = "Retrieve all procedures.Available to all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Procedures found"),
            @ApiResponse(responseCode = "404", description = "Procedures not found")
    })
    @GetMapping()
    List<ProcedureDto> findAll();

    @Operation(summary = "Get Procedure by ID (Public)",
            description = "Get a specific procedure by its ID. Public")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Procedure found"),
            @ApiResponse(responseCode = "404", description = "Procedure not found")
    })
    @GetMapping("/{id}")
    ProcedureDto findById(@Parameter(description = "Procedure ID", example = "1") @PathVariable("id") Long id);

    @Operation(summary = "Get Procedures by Category (Public)",
            description = "Get all procedures for a specific category. Public")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Procedure found"),
            @ApiResponse(responseCode = "404", description = "Procedure not found")
    })

    @GetMapping("/by-category/{categoryId}")
    List<ProcedureByCategoryDto> findProceduresByCategoryId(
            @Parameter(description = "Category ID", example = "1")
            @PathVariable("categoryId") Long categoryId);
}