package com.example.end.mail;

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
public class ProjectMailSender {

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

}
