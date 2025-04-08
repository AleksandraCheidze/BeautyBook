package com.example.end.infrastructure.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.api.ApiResponse;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {

    private static final Logger logger = LoggerFactory.getLogger(ImageUploadService.class);

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        logger.info("Starting image upload: {}", file.getOriginalFilename());

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("url").toString();

            logger.info("Image successfully uploaded: {}", imageUrl);

            if (uploadResult.containsKey("error")) {
                logger.error("Image upload error: {}", uploadResult.get("error"));
            }

            if (imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            return imageUrl;
        } catch (Exception e) {
            logger.error("Error during image upload: {}", e.getMessage(), e);
            throw new IOException("Image upload error", e);
        }
    }

    public void deleteImage(String publicId) throws IOException {
        logger.info("Deleting image with publicId: {}", publicId);

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Image successfully deleted: {}", publicId);
        } catch (Exception e) {
            logger.error("Error during image deletion: {}", e.getMessage(), e);
            throw new IOException("Image deletion error", e);
        }
    }

    public String extractPublicId(String url) {
        logger.debug("Extracting publicId from URL: {}", url);
        return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
    }

    public boolean exists(String publicId) {
        logger.info("Checking existence of image with publicId: {}", publicId);

        try {
            ApiResponse resource = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            boolean exists = resource != null && !resource.isEmpty();
            logger.info("Image {} exists: {}", publicId, exists);
            return exists;
        } catch (IOException e) {
            logger.warn("Error checking image existence: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during image check: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}