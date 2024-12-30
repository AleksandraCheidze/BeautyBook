package com.example.end.controller;
import org.springframework.http.MediaType;

import com.example.end.dto.UserDetailsDto;
import com.example.end.exceptions.InvalidFileException;
import com.example.end.service.interfaces.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/metadata/{userId}")
public class UserMetadataController {

    private final UserService userService;

    @PostMapping(value = "/profileImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadProfilePhoto(
            @PathVariable Long userId,
            @RequestPart("file") MultipartFile file) {
        try {
            long maxSize = 30 * 1024 * 1024; // Максимальный размер файла: 5 МБ

            String imageUrl = userService.uploadProfilePhoto(userId, file, maxSize);

            return UserDetailsDto.builder()
                    .id(userId)
                    .profileImageUrl(imageUrl)
                    .build();
        } catch (InvalidFileException e) {
            throw new InvalidFileException("Invalid file format or size.");
        } catch (IOException e) {
            throw new RuntimeException("Error during file upload", e);
        }
    }

    @PostMapping("/portfolioImages")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadPortfolioPhotos(@PathVariable Long userId, @RequestPart("files") List<MultipartFile> files) {
        try {
            long maxSize = 30 * 1024 * 1024; // 5 МБ

            List<String> uploadedUrls = userService.uploadPortfolioPhotos(userId, files, maxSize);

            return UserDetailsDto.builder()
                    .id(userId)
                    .portfolioImageUrls(uploadedUrls)
                    .build();
        } catch (InvalidFileException e) {
            throw new InvalidFileException("Invalid file format or size.");
        } catch (IOException e) {
            throw new RuntimeException("Error during file upload", e);
        }
    }

    @DeleteMapping("/profileImage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfilePhoto(@PathVariable Long userId) {
        try {
            userService.deleteProfilePhoto(userId);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting profile image", e);
        }
    }

    @DeleteMapping("/portfolioImage/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolioPhoto(@PathVariable Long userId, @PathVariable Long photoId) {
        try {
            userService.deletePortfolioPhoto(userId, photoId);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting portfolio image", e);
        }
    }
}