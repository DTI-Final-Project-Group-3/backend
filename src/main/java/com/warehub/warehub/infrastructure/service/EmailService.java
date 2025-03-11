package com.warehub.warehub.infrastructure.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Email Verification");
        message.setText("Click the link to verify your email: " + verificationLink);
        mailSender.send(message);
    }

    public void sendResetPasswordEmail(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset");
        message.setText("Click the link to reset your password: " + verificationLink);
        mailSender.send(message);
    }

    public void sendEmailChangeLink(String to, String verificationLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Change email");
        message.setText("Click here to verify your new email: " + verificationLink);
        mailSender.send(message);
    }
}