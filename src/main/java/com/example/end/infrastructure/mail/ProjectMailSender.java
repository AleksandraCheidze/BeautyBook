package com.example.end.infrastructure.mail;

import com.example.end.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class    ProjectMailSender {

    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}") // Внедряем имя пользователя (адрес эл. почты) из application.yml
    private String senderEmail;
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
            throw new IllegalStateException(e);
        }

        javaMailSender.send(message);
    }

    @Async
    public void sendMasterConfirmationRequest(String adminEmail, String masterName) {
        String subject = "Anfrage zur Bestätigung eines neuen Meisters";
        String text = String.format("Sehr geehrter Administrator,\n\n" +
                "Der Benutzer %s hat sich als Meister registriert und wartet auf Ihre Bestätigung.\n" +
                "Bitte bestätigen Sie dies im System.\n\n" +
                "Mit freundlichen Grüßen,\nIhr Verwaltungssystem.", masterName);
        sendEmail(adminEmail, subject, text); // Using the sendEmail method to send the message
    }

    //TODO add a check when is a MailException
    /**
     * Sends confirmation emails to a master user and an administrator.
     * <p>
     * This method is used to notify the master user that their registration is pending approval
     * and to inform the administrator about the pending request.
     * </p>
     *
     * @param masterUser the {@link User} object representing the master user who registered.
     *                   This object must contain valid email and name details.
     */

    public void sendConfirmationEmails(User masterUser, String adminEmail) {
        String subject = "Bestätigung der Registrierung des Meisters ausstehend";
        String messageToMaster = "Ihre Registrierung als Meister wurde erfasst und wartet auf die Bestätigung durch den Administrator. " +
                "Wir werden uns mit Ihnen in Verbindung setzen, sobald Ihr Konto bestätigt wurde. Vielen Dank für Ihre Registrierung!";
        sendEmail(masterUser.getEmail(), subject, messageToMaster);
        String messageToAdmin = masterUser.getFirstName() + " " + masterUser.getLastName();
        sendMasterConfirmationRequest(adminEmail, messageToAdmin);
    }

    //TODO add a check when is a MailException
    //TODO ==> in Sender
    /**
     * Sends a registration email to the specified user.
     *
     * <p>
     * This method is used to notify the user of a successful registration on the platform.
     * The email includes a congratulatory message and is sent to the email address
     * associated with the provided {@code User} object.
     * </p>
     *
     * @param user the user who has successfully registered. The user's email must be valid and non-null.
     */
    public void sendRegistrationEmail(User user) {
        String subject = "Registrierung auf der Website";
        String message = "Herzlichen Glückwunsch zur erfolgreichen Registrierung auf unserer Website!";
        sendEmail(user.getEmail(), subject, message);
    }

}
