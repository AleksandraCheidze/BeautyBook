package com.example.end.controller;

import com.example.end.controller.api.UserMetadataApi;
import com.example.end.dto.UserDetailsDto;
import com.example.end.dto.ErrorResponse;
import com.example.end.exceptions.InvalidFileException;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class UserMetadataController implements UserMetadataApi {

    private final UserService userService;

    @Override
    @PostMapping(value = "/{userId}/profileImage", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadProfilePhoto(@PathVariable Long userId, @RequestPart("file") MultipartFile file) throws IOException {
        try {
            long maxSize = 30 * 1024 * 1024; // 30 MB
            String imageUrl = userService.uploadProfilePhoto(userId, file, maxSize);
            return UserDetailsDto.builder()
                    .id(userId)
                    .profileImageUrl(imageUrl)
                    .build();
        } catch (InvalidFileException e) {
            throw new InvalidFileException("Invalid file format or size.");
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error during file upload", e);
        }
    }

    // Загружаем фотографии портфолио
    @Override
    @PostMapping(value = "/{userId}/portfolioImages", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadPortfolioPhotos(@PathVariable Long userId, @RequestPart("files") List<MultipartFile> files) throws IOException {
        try {
            long maxSize = 30 * 1024 * 1024; // 30 MB
            List<String> uploadedUrls = userService.uploadPortfolioPhotos(userId, files, maxSize);
            return UserDetailsDto.builder()
                    .id(userId)
                    .portfolioImageUrls(uploadedUrls)
                    .build();
        } catch (InvalidFileException e) {
            throw new InvalidFileException("Invalid file format or size.");
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error during file upload", e);
        }
    }

    // Удаляем профильную фотографию
    @Override
    @DeleteMapping("/{userId}/profileImage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfilePhoto(@PathVariable Long userId) throws IOException {
        try {
            userService.deleteProfilePhoto(userId);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error deleting profile image", e);
        }
    }

    // Удаляем фотографию из портфолио
    @Override
    @DeleteMapping("/{userId}/portfolioImage/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolioPhoto(@PathVariable Long userId, @PathVariable Long photoId) throws IOException {
        try {
            userService.deletePortfolioPhoto(userId, photoId);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error deleting portfolio image", e);
        }
    }
}
