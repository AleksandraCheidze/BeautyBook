package com.example.end.controller.api;

import com.example.end.dto.PortfolioImageDto;
import com.example.end.dto.ImageUrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Tag(name = "User Metadata", description = "Operations related to user metadata (photos)")
@RequestMapping("/api/users/{userId}/metadata")
public interface UserMetadataApi {

        @Operation(summary = "Upload Profile Photo", description = "Upload a profile photo for the user. Access: Authorized users only")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Profile photo uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUrlResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
                @ApiResponse(responseCode = "404", description = "User not found")
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(value = "/profile-photo", consumes = "multipart/form-data")
        ResponseEntity<ImageUrlResponse> uploadProfilePhoto(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId,
                @Parameter(description = "Profile photo file", required = true) @RequestPart("file") MultipartFile file)
                throws IOException;

        @Operation(summary = "Get Profile Photo", description = "Get user's profile photo. Access: All users")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Profile photo URL retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUrlResponse.class))),
                @ApiResponse(responseCode = "404", description = "Profile photo not found")
        })
        @GetMapping("/profile-photo")
        ResponseEntity<ImageUrlResponse> getProfilePhoto(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId)
                throws IOException;

        @Operation(summary = "Delete Profile Photo", description = "Delete user's profile photo. Access: Authorized users only")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Profile photo deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Profile photo not found")
        })
        @SecurityRequirement(name = "bearerAuth")
        @DeleteMapping("/profile-photo")
        ResponseEntity<Void> deleteProfilePhoto(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId)
                throws IOException, ExecutionException, InterruptedException;

        @Operation(summary = "Upload Portfolio Photos", description = "Upload multiple portfolio photos for the user. Access: Authorized users only")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Portfolio photos uploaded successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PortfolioImageDto.class))),
                @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
                @ApiResponse(responseCode = "404", description = "User not found")
        })
        @SecurityRequirement(name = "bearerAuth")
        @PostMapping(value = "/portfolio-photos", consumes = "multipart/form-data")
        List<PortfolioImageDto> uploadPortfolioPhotos(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId,
                @Parameter(description = "Portfolio photo files", required = true) @RequestPart("files") List<MultipartFile> files)
                throws IOException;

        @Operation(summary = "Get All Portfolio Photos", description = "Get all portfolio photos for a user. Access: All users")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Portfolio photos retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PortfolioImageDto.class))),
                @ApiResponse(responseCode = "404", description = "User not found")
        })
        @GetMapping("/portfolio-photos")
        List<PortfolioImageDto> getAllPortfolioPhotos(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId)
                throws IOException;

        @Operation(summary = "Get Portfolio Photo", description = "Get specific portfolio photo. Access: All users")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Portfolio photo URL retrieved successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ImageUrlResponse.class))),
                @ApiResponse(responseCode = "404", description = "Portfolio photo not found")
        })
        @GetMapping("/portfolio-photos/{photoId}")
        ResponseEntity<ImageUrlResponse> getPortfolioPhoto(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId,
                @Parameter(description = "ID of the portfolio photo", required = true) @PathVariable Long photoId)
                throws IOException;

        @Operation(summary = "Delete Portfolio Photo", description = "Delete specific portfolio photo. Access: Authorized users only")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Portfolio photo deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Portfolio photo not found")
        })
        @SecurityRequirement(name = "bearerAuth")
        @DeleteMapping("/portfolio-photos/{photoId}")
        ResponseEntity<Void> deletePortfolioPhoto(
                @Parameter(description = "ID of the user", required = true) @PathVariable Long userId,
                @Parameter(description = "ID of the portfolio photo", required = true) @PathVariable Long photoId)
                throws IOException;

}
