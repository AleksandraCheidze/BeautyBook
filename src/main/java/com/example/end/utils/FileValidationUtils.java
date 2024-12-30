package com.example.end.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class FileValidationUtils {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 30 * 1024 * 1024;

    public static boolean isValidImage(MultipartFile file) {

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            return false;
        }
        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            return false;
        }


        if (file.getSize() > MAX_FILE_SIZE) {
            return false;
        }

        return true;
    }

    private static String getFileExtension(String fileName) {
        int indexOfDot = fileName.lastIndexOf(".");
        if (indexOfDot == -1) {
            return "";
        }
        return fileName.substring(indexOfDot + 1);
    }
}
