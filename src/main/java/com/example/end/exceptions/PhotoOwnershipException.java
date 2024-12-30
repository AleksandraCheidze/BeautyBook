package com.example.end.exceptions;

public class PhotoOwnershipException extends RuntimeException {
    public PhotoOwnershipException(String message) {
        super(message);
    }

    public static PhotoOwnershipException notBelongToUser(Long photoId, Long userId) {
        return new PhotoOwnershipException("Photo with ID " + photoId + " does not belong to user with ID " + userId);
    }
}
