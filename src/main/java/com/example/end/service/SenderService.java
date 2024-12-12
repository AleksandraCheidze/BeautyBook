package com.example.end.service;

import com.example.end.infrastructure.mail.ProjectMailSender;
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
    private String senderEmail;

    /**
     * Sends a message to the administrator with details provided by the user.
     *
     * @param email     the email address of the user sending the message. Must be a valid email.
     * @param phone     the phone number of the user sending the message. Can be null or empty if unavailable.
     * @param firstName the first name of the user sending the message. Must be non-null and not empty.
     * @param lastName  the last name of the user sending the message. Must be non-null and not empty.
     * @param message   the content of the message being sent to the administrator. Must be non-null.
     */
    public void sendMessageToAdmin(String email, String phone, String firstName, String lastName, String message) {
        if (!StringUtils.hasText(email) || !StringUtils.hasText(firstName) || !StringUtils.hasText(lastName) || !StringUtils.hasText(message)) {
            throw new IllegalArgumentException("Pflichtfelder (E-Mail, Vorname, Nachname, Nachricht) dürfen nicht leer oder null sein.");
        }

        String subject = "Neue Nachricht vom Benutzer: " + firstName + " " + lastName;
        String messageBody = String.format(
                "E-Mail: %s\nTelefon: %s\nNachricht: %s",
                email,
                phone != null ? phone : "Nicht angegeben",
                message
        );

        try {
            mailSender.sendEmail(senderEmail, subject, messageBody);
        } catch (MailException ex) {
            throw new RuntimeException("Die Nachricht konnte nicht an den Administrator gesendet werden. Bitte versuchen Sie es später erneut.", ex);
        }
    }
}

