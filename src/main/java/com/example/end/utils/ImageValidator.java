package com.example.end.utils;

import com.example.end.exceptions.InvalidFileException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ImageValidator {

    private static final List<String> ALLOWED_FORMATS = List.of("image/jpeg", "image/png", "image/gif");

    public static void validateImage(MultipartFile file, long maxSize) {
        if (!ALLOWED_FORMATS.contains(file.getContentType())) {
            throw new InvalidFileException("Only JPEG, PNG, or GIF images are allowed.");
        }
        if (file.getSize() > maxSize) {
            throw new InvalidFileException("File size exceeds the maximum allowed size of " + maxSize + " bytes.");
        }
    }
}
