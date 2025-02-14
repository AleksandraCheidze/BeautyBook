package com.example.end.exceptions;

import org.springframework.http.HttpStatus;

/**
 * Custom exception to indicate that a user was not found or does not meet specific conditions.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message to include in the exception
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserNotFoundException with the specified detail message and cause.
     *
     * @param message the detail message to include in the exception
     * @param cause   the cause of the exception
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Factory method to create an exception for a user not found by ID.
     *
     * @param userId the ID of the user that was not found
     * @return a UserNotFoundException with a preformatted message
     */
    public static UserNotFoundException byId(Long userId) {
        return new UserNotFoundException("User not found with ID: " + userId);
    }

    /**
     * Factory method to create an exception for a user not found by email.
     *
     * @param email the email of the user that was not found
     * @return a UserNotFoundException with a preformatted message
     */
    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }

    /**
     * Factory method to create an exception for a user not being a MASTER role.
     *
     * @param email the email of the user that is not a MASTER
     * @return a UserNotFoundException with a preformatted message
     */
    public static UserNotFoundException notMaster(String email) {
        return new UserNotFoundException("User with email " + email + " is not a MASTER.");
    }

    /**
     * Factory method to create an exception for a master user already being active.
     *
     * @param email the email of the master user that is already active
     * @return a UserNotFoundException with a preformatted message
     */
    public static UserNotFoundException alreadyActive(String email) {
        return new UserNotFoundException("Master user with email " + email + " is already active.");
    }
    /**
     * Factory method to create an exception for a user not found for a category by its ID.
     *
     * @param categoryId the ID of the category for which the user was not found
     * @return a UserNotFoundException with a preformatted message
     */
    public static UserNotFoundException forCategoryId(Long categoryId) {
        return new UserNotFoundException("User for category with ID " + categoryId + " not found.");
    }
}
