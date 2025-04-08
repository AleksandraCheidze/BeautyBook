package com.example.end.service;

import com.example.end.dto.PortfolioImageDto;
import com.example.end.infrastructure.config.ImageUploadService;
import com.example.end.infrastructure.exceptions.ForbiddenException;
import com.example.end.infrastructure.exceptions.ImageDeleteException;
import com.example.end.infrastructure.exceptions.ImageUploadException;
import com.example.end.infrastructure.exceptions.ResourceNotFoundException;
import com.example.end.models.PortfolioPhoto;
import com.example.end.models.User;
import com.example.end.repository.PortfolioPhotoRepository;
import com.example.end.repository.UserRepository;
import com.example.end.utils.FileValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserMetadataService {
    private final PortfolioPhotoRepository portfolioPhotoRepository;
    private final ImageUploadService imageUploadService;
    private final UserServiceImpl userService;
    private final UserRepository userRepository;

    public String uploadProfilePhoto(Long userId, MultipartFile file) throws IOException {
        FileValidationUtils.validateImage(file);
        User user = userService.findUserByIdOrThrow(userId);

        try {
            String imageUrl = imageUploadService.uploadImage(file);
            user.setProfilePhotoUrl(imageUrl);
            userRepository.save(user);
            return imageUrl;
        } catch (Exception e) {
            throw new ImageUploadException("Error during image upload.", e);
        }
    }


    public List<PortfolioImageDto> uploadPortfolioPhotos(Long userId, List<MultipartFile> files) throws IOException {
        files.forEach(FileValidationUtils::validateImage);
        User user = userService.findUserByIdOrThrow(userId);
        List<PortfolioImageDto> uploadedPhotos = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                String imageUrl = imageUploadService.uploadImage(file);
                PortfolioPhoto portfolioPhoto = new PortfolioPhoto();
                portfolioPhoto.setUrl(imageUrl);
                portfolioPhoto.setUser(user);

                PortfolioPhoto savedPhoto = portfolioPhotoRepository.save(portfolioPhoto);
                uploadedPhotos.add(
                        PortfolioImageDto.builder()
                                .id(savedPhoto.getId())
                                .url(savedPhoto.getUrl())
                                .build()
                );
            } catch (Exception e) {
                throw new ImageUploadException("Error uploading image for user " + userId, e);
            }
        }
        return uploadedPhotos;
    }

    @Transactional
    public void deleteProfilePhoto(Long userId) {
        User user = userService.findUserByIdOrThrow(userId);
        String profilePhotoUrl = user.getProfilePhotoUrl();
        if (profilePhotoUrl == null) {
            throw new ResourceNotFoundException("Profile photo not found for user ID: " + userId);
        }
        try {
            String publicId = imageUploadService.extractPublicId(profilePhotoUrl);
            imageUploadService.deleteImage(publicId);
            user.setProfilePhotoUrl(null);
            userRepository.save(user);
        } catch (Exception e) {
            throw new ImageDeleteException("Failed to delete profile photo", e);
        }
    }

    @Transactional
    public void deletePortfolioPhoto(Long userId, Long photoId) {
        PortfolioPhoto photo = portfolioPhotoRepository.findById(photoId)
                .orElseThrow(() -> {
                    return new ResourceNotFoundException("Portfolio photo not found with ID: " + photoId);
                });
        if (!photo.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Photo does not belong to the specified user");
        }
        try {
            String publicId = imageUploadService.extractPublicId(photo.getUrl());
            imageUploadService.deleteImage(publicId);
            portfolioPhotoRepository.delete(photo);
        } catch (Exception e) {
            throw new ImageDeleteException("Failed to delete portfolio photo", e);
        }
    }
}
