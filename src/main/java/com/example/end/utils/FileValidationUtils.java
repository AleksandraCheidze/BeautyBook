package com.example.end.utils;

import com.example.end.infrastructure.exceptions.InvalidFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidationUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileValidationUtils.class);
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    public static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50 MB

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File cannot be null or empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new InvalidFileException("File name cannot be null");
        }

        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new InvalidFileException(String.format(
                    "Invalid file format. Allowed formats are: %s",
                    String.join(", ", ALLOWED_EXTENSIONS)));
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(String.format(
                    "File size exceeds the limit. Maximum allowed size is %d MB",
                    MAX_FILE_SIZE / (1024 * 1024)));
        }
    }

    private static String getFileExtension(String fileName) {
        int indexOfDot = fileName.lastIndexOf(".");
        return indexOfDot == -1 ? "" : fileName.substring(indexOfDot + 1);
    }
}