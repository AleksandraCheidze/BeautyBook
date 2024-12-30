package com.example.end.exceptions;

public class PhotoNotFoundException extends RuntimeException {
    public PhotoNotFoundException(String message) {
        super(message);
    }

    public static PhotoNotFoundException forId(Long photoId) {
        return new PhotoNotFoundException("Photo with ID " + photoId + " not found");
    }
}
