package com.example.end.controller.api;

import com.example.end.dto.BookingDto;
import com.example.end.dto.PortfolioImageDto;
import com.example.end.dto.ProfileImageDto;
import com.example.end.dto.UserDetailsDto;
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
import org.springframework.web.bind.annotation.*;

@Tags(value = {
        @Tag(name = "UserMetadata", description = "Operations related to users metadata")
})
@RequestMapping("/api/metadata/{userId}")
public interface UserMetadataApi {
    @Operation(summary = "Create a profile image.Available to all authorized MASTER", description = "Available to all authorized MASTER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Image was successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorsDto.class)))
    })
    @PostMapping("/profileImages")
    @ResponseStatus(HttpStatus.CREATED)
    UserDetailsDto addProfileImages(
    @Parameter(description = "ID of the user to be updated. Cannot be empty.",
            required = true) @PathVariable ("userId")
    Long userId,
    @Parameter(description = "User profile image to be added.",
            required = true) @RequestBody
    @Valid ProfileImageDto profileImageDto);

    @Operation(summary = "Create a portfolio images.Available to all authorized MASTER", description = "Available to all users.Available to all authorized MASTER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Image was successfully created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BookingDto.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ValidationErrorsDto.class)))
    })
    @PostMapping("/portfolioImage")
    @ResponseStatus(HttpStatus.CREATED)
    UserDetailsDto addPortfolioImages(
            @Parameter(description = "ID of the user to be updated. Cannot be empty.",
                    required = true) @PathVariable ("userId")
            Long userId,
            @Parameter(description = "User profile image to be added.",
                    required = true) @RequestBody
            @Valid PortfolioImageDto portfolioImageDto);
}
