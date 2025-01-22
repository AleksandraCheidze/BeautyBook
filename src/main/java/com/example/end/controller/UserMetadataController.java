package com.example.end.controller;

import com.example.end.controller.api.UserMetadataApi;
import com.example.end.dto.PortfolioImageDto;
import com.example.end.dto.UserDetailsDto;
import com.example.end.exceptions.InvalidFileException;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class UserMetadataController implements UserMetadataApi {

    private final UserService userService;

    @Override
    @PostMapping(value = "/{userId}/profileImage", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadProfilePhoto(@PathVariable Long userId, @RequestPart("file") MultipartFile file) {
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

    @Override
    @PostMapping(value = "/{userId}/portfolioImages", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadPortfolioPhotos(@PathVariable Long userId, @RequestPart("files") List<MultipartFile> files) {
        try {
            long maxSize = 30 * 1024 * 1024; // 30 MB
            List<PortfolioImageDto> uploadedUrls = userService.uploadPortfolioPhotos(userId, files, maxSize);
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

    @Override
    @DeleteMapping("/{userId}/profileImage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfilePhoto(@PathVariable Long userId){
        try {
            userService.deleteProfilePhoto(userId);
        } catch (IOException | ExecutionException | InterruptedException e) {
            throw new RuntimeException("Error deleting profile image", e);
        }
    }

    @Override
    @DeleteMapping("/{userId}/portfolioImage/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolioPhoto(@PathVariable Long userId, @PathVariable Long photoId) {
        try {
            userService.deletePortfolioPhoto(userId, photoId);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting portfolio image", e);
        }
    }


}
