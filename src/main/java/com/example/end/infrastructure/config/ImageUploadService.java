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
        logger.info("Начало загрузки изображения: {}", file.getOriginalFilename());

        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String imageUrl = uploadResult.get("url").toString();

            logger.info("Изображение успешно загружено: {}", imageUrl);

            if (uploadResult.containsKey("error")) {
                logger.error("Ошибка загрузки изображения: {}", uploadResult.get("error"));
            }

            if (imageUrl.startsWith("http://")) {
                imageUrl = imageUrl.replace("http://", "https://");
            }

            return imageUrl;
        } catch (Exception e) {
            logger.error("Ошибка при загрузке изображения: {}", e.getMessage(), e);
            throw new IOException("Ошибка загрузки изображения", e);
        }
    }

    public void deleteImage(String publicId) throws IOException {
        logger.info("Удаление изображения с publicId: {}", publicId);

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            logger.info("Изображение успешно удалено: {}", publicId);
        } catch (Exception e) {
            logger.error("Ошибка при удалении изображения: {}", e.getMessage(), e);
            throw new IOException("Ошибка удаления изображения", e);
        }
    }

    public String extractPublicId(String url) {
        logger.debug("Извлечение publicId из URL: {}", url);
        return url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
    }

    public boolean exists(String publicId) {
        logger.info("Проверка существования изображения с publicId: {}", publicId);

        try {
            ApiResponse resource = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            boolean exists = resource != null && !resource.isEmpty();
            logger.info("Изображение {} существует: {}", publicId, exists);
            return exists;
        } catch (IOException e) {
            logger.warn("Ошибка при проверке существования изображения: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при проверке изображения: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
