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
    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    public static boolean isValidImage(MultipartFile file) {

        if (file == null) {
            logger.warn("File is null.");
            return true;
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            logger.warn("File name is null.");
            return true;
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            logger.warn("Invalid file extension: {}", fileExtension);
            return true;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            logger.warn("File size exceeds the limit: {} bytes. Max allowed size is {} bytes.", file.getSize(), MAX_FILE_SIZE);
            return true;
        }

        logger.info("File validation passed for file: {}", fileName);
        return false;
    }

    private static String getFileExtension(String fileName) {
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "";
        }
        return fileName.substring(indexOfDot + 1);
    }
}
