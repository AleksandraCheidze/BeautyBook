package com.example.end.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileValidationUtils.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    // Увеличим размер до 50 МБ
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024;

    public static String validateImage(MultipartFile file) {
        if (file == null) {
            logger.warn("File is null.");
            return "File cannot be null";
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            logger.warn("File name is null.");
            return "File name cannot be null";
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            logger.warn("Invalid file extension: {}", fileExtension);
            return String.format("Invalid file format. Allowed formats are: %s", String.join(", ", ALLOWED_EXTENSIONS));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("File size exceeds the limit: {} bytes. Max allowed size is {} bytes.", file.getSize(),
                    MAX_FILE_SIZE);
            return String.format("File size exceeds the limit. Maximum allowed size is %d MB",
                    MAX_FILE_SIZE / (1024 * 1024));
        }

        logger.info("File validation passed for file: {}", fileName);
        return null;
    }

    private static String getFileExtension(String fileName) {
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "";
        }
        return fileName.substring(indexOfDot + 1);
    }
}
