package com.example.end.infrastructure.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProjectMailSender {

    private final JavaMailSender javaMailSender;
    @Value("${MAIL_USERNAME}")
    private String senderEmail;

    /**
     * Sends an email to the specified recipient.
     * <p>
     * This method is used to send any kind of email message with the provided recipient email, subject, and content.
     * </p>
     *
     * @param email - The email address of the recipient.
     * @param subject - The subject of the email.
     * @param text - The body content of the email.
     */
    @Async
    public void sendEmail(String email, String subject, String text) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");

        try {
            helper.setFrom(senderEmail);
            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(text, true);
        } catch (MessagingException e) {
            throw new IllegalStateException("Error creating message", e);
        }

        javaMailSender.send(message);
    }

    /**
     * Sends a request email to the administrator for the confirmation of a new master.
     * <p>
     * This method is used to notify the administrator that a new user has registered as a master
     * and is awaiting confirmation.
     * </p>
     *
     * @param adminEmail - The email address of the administrator.
     * @param masterName - The name of the master who needs confirmation.
     */
    @Async
    public void sendMasterConfirmationRequest(String adminEmail, String masterName) {
        String subject = "Anfrage zur Bestätigung eines neuen Meisters";
        String text = String.format("Sehr geehrter Administrator,\n\n" +
                "Der Benutzer %s hat sich als Meister registriert und wartet auf Ihre Bestätigung.\n" +
                "Bitte bestätigen Sie dies im System.\n\n" +
                "Mit freundlichen Grüßen,\nIhr Verwaltungssystem.", masterName);
        sendEmail(adminEmail, subject, text);
    }

    /**
     * Sends confirmation emails to the master and the administrator.
     * <p>
     * This method notifies the master about the pending confirmation and informs the administrator about the
     * master’s registration request.
     * </p>
     *
     * @param adminEmail - The email address of the administrator.
     * @param masterEmail - The email address of the master.
     * @param masterName - The name of the master.
     */
    public void sendConfirmationEmails(String adminEmail, String masterEmail, String masterName) {
        String subject = "Bestätigung der Registrierung des Meisters";
        String messageToMaster = "Ihre Registrierung als Meister wurde erfolgreich erfasst und wartet auf die Bestätigung des Administrators.";
        sendEmail(masterEmail, subject, messageToMaster);
        sendMasterConfirmationRequest(adminEmail, masterName);
    }

    /**
     * Sends a registration email to a user.
     * <p>
     * This method is used to inform the user that they have successfully registered on the platform.
     * </p>
     *
     * @param email - The email address of the user.
     */
    public void sendRegistrationEmail(String email) {
        String subject = "Herzlichen Glückwunsch zur erfolgreichen Registrierung!";
        String message = "Vielen Dank für Ihre Registrierung auf unserer Plattform!";
        sendEmail(email, subject, message);
    }
}
