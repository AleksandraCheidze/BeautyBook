package com.example.end.controller;

import com.example.end.controller.api.UserMetadataApi;
import com.example.end.dto.PortfolioImageDto;
import com.example.end.dto.UserDetailsDto;
import com.example.end.exceptions.InvalidFileException;
import com.example.end.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(UserMetadataController.class);

    private final UserService userService;

    @Override
    @PostMapping(value = "/{userId}/profileImage", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadProfilePhoto(@PathVariable Long userId, @RequestPart("file") MultipartFile file) {
        logger.info("Начало загрузки фото профиля для пользователя с ID: {}", userId);

        try {
            long maxSize = 30 * 1024 * 1024; // 30 MB
            String imageUrl = userService.uploadProfilePhoto(userId, file, maxSize);

            logger.info("Фото профиля загружено успешно: {}", imageUrl);

            return UserDetailsDto.builder()
                    .id(userId)
                    .profileImageUrl(imageUrl)
                    .build();
        } catch (InvalidFileException e) {
            logger.warn("Некорректный формат или размер файла для пользователя ID: {}", userId);
            throw new InvalidFileException("Invalid file format or size.");
        } catch (IOException | ExecutionException | InterruptedException e) {
            logger.error("Ошибка загрузки фото профиля пользователя ID: {}", userId, e);
            throw new RuntimeException("Error during file upload", e);
        }
    }

    @Override
    @PostMapping(value = "/{userId}/portfolioImages", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailsDto uploadPortfolioPhotos(@PathVariable Long userId, @RequestPart("files") List<MultipartFile> files) {
        logger.info("Начало загрузки портфолио для пользователя с ID: {}", userId);

        try {
            long maxSize = 30 * 1024 * 1024; // 30 MB
            List<PortfolioImageDto> uploadedUrls = userService.uploadPortfolioPhotos(userId, files, maxSize);

            logger.info("Портфолио загружено успешно: {} файлов", uploadedUrls.size());

            return UserDetailsDto.builder()
                    .id(userId)
                    .portfolioImageUrls(uploadedUrls)
                    .build();
        } catch (InvalidFileException e) {
            logger.warn("Некорректный формат или размер файлов для пользователя ID: {}", userId);
            throw new InvalidFileException("Invalid file format or size.");
        } catch (IOException | ExecutionException | InterruptedException e) {
            logger.error("Ошибка загрузки портфолио пользователя ID: {}", userId, e);
            throw new RuntimeException("Error during file upload", e);
        }
    }

    @Override
    @DeleteMapping("/{userId}/profileImage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfilePhoto(@PathVariable Long userId) {
        logger.info("Запрос на удаление фото профиля пользователя ID: {}", userId);

        try {
            userService.deleteProfilePhoto(userId);
            logger.info("Фото профиля удалено успешно для пользователя ID: {}", userId);
        } catch (IOException | ExecutionException | InterruptedException e) {
            logger.error("Ошибка удаления фото профиля пользователя ID: {}", userId, e);
            throw new RuntimeException("Error deleting profile image", e);
        }
    }

    @Override
    @DeleteMapping("/{userId}/portfolioImage/{photoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortfolioPhoto(@PathVariable Long userId, @PathVariable Long photoId) {
        logger.info("Запрос на удаление фото ID: {} из портфолио пользователя ID: {}", photoId, userId);

        try {
            userService.deletePortfolioPhoto(userId, photoId);
            logger.info("Фото портфолио ID: {} успешно удалено для пользователя ID: {}", photoId, userId);
        } catch (IOException e) {
            logger.error("Ошибка удаления фото портфолио ID: {} пользователя ID: {}", photoId, userId, e);
            throw new RuntimeException("Error deleting portfolio image", e);
        }
    }
}
