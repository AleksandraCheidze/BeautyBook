package com.example.end.service;

import com.example.end.infrastructure.mail.ProjectMailSender;
import com.example.end.models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SenderService {

    private final ProjectMailSender mailSender;

    @Value("${spring.mail.admin-email}")
    private String adminEmail;

    /**
     * Sends a message to the administrator with details provided by the user.
     * <p>
     * This method is used to send a message from the user to the administrator, including the user's email, phone,
     * first name, last name, and the message content.
     * </p>
     *
     * @param email - The email address of the user sending the message.
     * @param phone - The phone number of the user (can be null).
     * @param firstName - The first name of the user.
     * @param lastName - The last name of the user.
     * @param message - The content of the message sent to the administrator.
     */
    public void sendMessageToAdmin(String email, String phone, String firstName, String lastName, String message) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(firstName) || !StringUtils.hasText(lastName) || !StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Mandatory fields (email, firstName, lastName, message) cannot be empty or null.");
        }

        String subject = "Neue Nachricht vom Benutzer: " + firstName + " " + lastName;
        String messageBody = String.format("E-Mail: %s\nTelefon: %s\nNachricht: %s", email, phone != null ? phone : "Nicht angegeben", message);

        try {
            mailSender.sendEmail(adminEmail, subject, messageBody);
        } catch (MailException ex) {
            throw new RuntimeException("Failed to send the message to the administrator. Please try again later.", ex);
        }
    }

    /**
     * Sends confirmation email to the master user and a request to the administrator for confirmation.
     * <p>
     * This method is used to notify the master user about the pending confirmation and sends a request to the
     * administrator for approval.
     * </p>
     *
     * @param user - The user object representing the master who registered.
     */
    public void sendMasterRegistrationConfirmation(User user) {

        if (user.getRole() != User.Role.MASTER) {
            throw new IllegalArgumentException("This user is not a master.");
        }

        String subject = "Ihre Registrierung als Meister wartet auf Bestätigung";
        String messageToMaster = "Ihre Registrierung als Meister wurde erfolgreich erfasst und wartet auf die Bestätigung des Administrators.";
        mailSender.sendEmail(user.getEmail(), subject, messageToMaster);

        String messageToAdmin = user.getFirstName() + " " + user.getLastName();
        mailSender.sendMasterConfirmationRequest(adminEmail, messageToAdmin);
    }
}
