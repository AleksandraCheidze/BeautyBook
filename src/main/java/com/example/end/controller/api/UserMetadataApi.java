package com.example.end.controller.api;

import com.example.end.dto.UserDetailsDto;
import com.example.end.models.User;
import com.example.end.validation.dto.ValidationErrorsDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * API interface for managing user metadata such as profile and portfolio images.
 */
@RequestMapping("/api/metadata")
public interface UserMetadataApi {

    @Operation(summary = "Upload profile photo", description = "Upload a user's profile photo. Available to authorized users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profile photo uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class)))
    })
    @PostMapping(value = "/{userId}/profileImage", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    UserDetailsDto uploadProfilePhoto(@PathVariable Long userId, @RequestPart("file") MultipartFile file) throws IOException;

    @Operation(summary = "Upload portfolio photos", description = "Upload portfolio photos for a user. Available to authorized users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Portfolio photos uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDetailsDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid files", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ValidationErrorsDto.class)))
    })
    @PostMapping(value = "/{userId}/portfolioImages", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    UserDetailsDto uploadPortfolioPhotos(@PathVariable Long userId, @RequestParam("files") List<MultipartFile> files) throws IOException;



    @Operation(summary = "Delete profile photo", description = "Delete a user's profile photo. Available to authorized users.")
    @ApiResponse(responseCode = "204", description = "Profile photo deleted successfully")
    @DeleteMapping("/{userId}/profileImage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteProfilePhoto(@PathVariable Long userId) throws IOException;

    @Operation(summary = "Delete portfolio photo", description = "Delete a specific portfolio photo for a user. Available to authorized users.")
    @ApiResponse(responseCode = "204", description = "Portfolio photo deleted successfully")
    @DeleteMapping("/{userId}/portfolioImage/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deletePortfolioPhoto(@PathVariable Long userId, @PathVariable Long photoId) throws IOException;
}
