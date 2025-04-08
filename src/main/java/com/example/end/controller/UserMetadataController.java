package com.example.end.controller;

import com.example.end.controller.api.UserMetadataApi;
import com.example.end.dto.PortfolioImageDto;
import com.example.end.dto.UserDetailsDto;
import com.example.end.service.UserMetadataService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RestController
public class UserMetadataController implements UserMetadataApi {

    private static final Logger logger = LoggerFactory.getLogger(UserMetadataController.class);
    private final UserMetadataService userMetadataService;

    @Override
    public UserDetailsDto uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {
        logger.info("Request received to upload profile photo for user ID: {}", userId);

        String imageUrl = userMetadataService.uploadProfilePhoto(userId, file);

        logger.info("Profile photo uploaded successfully for user ID: {}", userId);
        return UserDetailsDto.builder()
                .id(userId)
                .profileImageUrl(imageUrl)
                .build();
    }

    @Override
    public UserDetailsDto uploadPortfolioPhotos(Long userId, List<MultipartFile> files) throws IOException {
        logger.info("Request received to upload portfolio photos for user ID: {}, files count: {}", userId, files.size());

        List<PortfolioImageDto> uploadedUrls = userMetadataService.uploadPortfolioPhotos(userId, files);

        logger.info("Portfolio photos uploaded successfully for user ID: {}, uploaded count: {}", userId, uploadedUrls.size());
        return UserDetailsDto.builder()
                .id(userId)
                .portfolioImageUrls(uploadedUrls)
                .build();
    }

    @Override
    public void deleteProfilePhoto(Long userId) {
        logger.info("Request to delete profile photo for user ID: {}", userId);
        userMetadataService.deleteProfilePhoto(userId);
    }

    @Override
    public void deletePortfolioPhoto(Long userId, Long photoId) {
        logger.info("Request to delete portfolio photo ID: {} for user ID: {}", photoId, userId);
        userMetadataService.deletePortfolioPhoto(userId, photoId);
    }
}
