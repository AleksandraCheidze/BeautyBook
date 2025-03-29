package com.example.end.controller;

import com.example.end.controller.api.UserMetadataApi;
import com.example.end.dto.ImageUrlResponse;
import com.example.end.dto.PortfolioImageDto;
import com.example.end.infrastructure.exceptions.InvalidFileException;
import com.example.end.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users/{userId}/metadata")
@Tag(name = "User Metadata", description = "Operations related to user metadata (photos)")
public class UserMetadataController implements UserMetadataApi {
    private static final Logger logger = LoggerFactory.getLogger(UserMetadataController.class);
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    private final UserService userService;

    @Override
    @PostMapping(value = "/profile-photo", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "Upload Profile Photo", description = "Upload a profile photo for the user. Access: Authorized users only")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ImageUrlResponse> uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestPart("file") MultipartFile file) throws IOException {

        logger.info("Starting profile photo upload for user ID: {}", userId);

        try {
            String imageUrl = userService.uploadProfilePhoto(userId, file, MAX_FILE_SIZE);
            logger.info("Profile photo uploaded successfully: {}", imageUrl);
            return ResponseEntity.ok(new ImageUrlResponse(imageUrl));
        } catch (InvalidFileException e) {
            logger.warn("Invalid file format or size for user ID: {}", userId);
            throw e;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error uploading profile photo for user ID: {}", userId, e);
            throw new RuntimeException("Error during file upload", e);
        }
    }

    @Override
    @GetMapping("/profile-photo")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get Profile Photo", description = "Get user's profile photo. Access: All users")
    public ResponseEntity<ImageUrlResponse> getProfilePhoto(@PathVariable Long userId) throws IOException {
        String imageUrl = userService.getProfilePhoto(userId);
        return ResponseEntity.ok(new ImageUrlResponse(imageUrl));
    }

    @Override
    @DeleteMapping("/profile-photo")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "Delete Profile Photo", description = "Delete user's profile photo. Access: Authorized users only")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deleteProfilePhoto(@PathVariable Long userId)
            throws IOException, ExecutionException, InterruptedException {

        logger.info("Starting profile photo deletion for user ID: {}", userId);
        userService.deleteProfilePhoto(userId);
        logger.info("Profile photo deleted successfully for user ID: {}", userId);

        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping(value = "/portfolio-photos", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "Upload Portfolio Photos", description = "Upload multiple portfolio photos for the user. Access: Authorized users only")
    @SecurityRequirement(name = "bearerAuth")
    public List<PortfolioImageDto> uploadPortfolioPhotos(
            @PathVariable Long userId,
            @RequestPart("files") List<MultipartFile> files) throws IOException {

        logger.info("Starting portfolio upload for user ID: {}", userId);

        try {
            List<PortfolioImageDto> uploadedUrls = userService.uploadPortfolioPhotos(userId, files, MAX_FILE_SIZE);
            logger.info("Portfolio uploaded successfully: {} files", uploadedUrls.size());
            return uploadedUrls;
        } catch (InvalidFileException e) {
            logger.warn("Invalid file format or size for user ID: {}", userId);
            throw e;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("Error uploading portfolio for user ID: {}", userId, e);
            throw new RuntimeException("Error during file upload", e);
        }
    }

    @Override
    @GetMapping("/portfolio-photos")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get All Portfolio Photos", description = "Get all portfolio photos for the user. Access: All users")
    public List<PortfolioImageDto> getAllPortfolioPhotos(@PathVariable Long userId) throws IOException {
        logger.info("Retrieving all portfolio photos for user ID: {}", userId);
        return userService.getAllPortfolioPhotos(userId);
    }

    @Override
    @GetMapping("/portfolio-photos/{photoId}")
    @PreAuthorize("permitAll()")
    @Operation(summary = "Get Portfolio Photo", description = "Get specific portfolio photo. Access: All users")
    public ResponseEntity<ImageUrlResponse> getPortfolioPhoto(
            @PathVariable Long userId,
            @PathVariable Long photoId) throws IOException {
        String imageUrl = userService.getPortfolioPhoto(userId, photoId);
        return ResponseEntity.ok(new ImageUrlResponse(imageUrl));
    }

    @Override
    @DeleteMapping("/portfolio-photos/{photoId}")
    @PreAuthorize("hasRole('MASTER')")
    @Operation(summary = "Delete Portfolio Photo", description = "Delete specific portfolio photo. Access: Authorized users only")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> deletePortfolioPhoto(
            @PathVariable Long userId,
            @PathVariable Long photoId) throws IOException {
        logger.info("Starting portfolio photo deletion for user ID: {} and photo ID: {}", userId, photoId);
        userService.deletePortfolioPhoto(userId, photoId);
        logger.info("Portfolio photo deleted successfully for user ID: {} and photo ID: {}", userId, photoId);

        return ResponseEntity.ok().build();
    }
}
