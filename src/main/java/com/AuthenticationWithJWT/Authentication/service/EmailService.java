package com.AuthenticationWithJWT.Authentication.service;

import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.internet.MimeMessage;
import java.io.File;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            logger.info("Sending email to {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", toEmail, e);
        }
    }

    public void sendEmailWithAttachment(String toEmail, String subject, String body, String attachmentPath) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment(file.getFilename(), file);

            mailSender.send(message);
            logger.info("Email sent successfully to {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}", toEmail, e);
        }
    }
}
