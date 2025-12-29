package com.example.mywalletapp.service.serviceimpl;

import com.example.mywalletapp.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    // Inject base URL from application.properties
    @Value("${app.base-url}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        sendEmail(to, "Verify Your Email", baseUrl + "/verify-email?token=" + token,
                "Please verify your email by clicking the link: ");
    }

    public void sendPasswordResetEmail(String to, String token) {
        sendEmail(to, "Password Reset Request", baseUrl + "/reset-password?token=" + token,
                "To reset your password, click the following link: ");
    }

    private void sendEmail(String to, String subject, String url, String messagePrefix) {
        String message = messagePrefix + url;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        mailSender.send(email);
        logger.info("{} email sent to: {}", subject, to);
    }
}




//package com.example.mywalletapp.service.serviceimpl;
//
//import com.example.mywalletapp.service.EmailService;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class EmailServiceImpl implements EmailService {
//    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
//    private final JavaMailSender mailSender;
//
//    @Override
//    public void sendVerificationEmail(String to, String token) {
//        sendEmail(to,  "Verify Your Email", "http://localhost:8098/verify-email?token=" + token,
//                "Please verify your email by clicking the link: ");
//    }
//
//    public void sendPasswordResetEmail(String to, String token) {
//        sendEmail(to, "Password Reset Request", "http://localhost:8098/reset-password?token=" + token,
//                "To reset your password, click the following link: ");
//    }
//
//    private void sendEmail(String to, String subject, String url, String messagePrefix) {
//        String message = messagePrefix + url;
//
//        SimpleMailMessage email = new SimpleMailMessage();
//        email.setTo(to);
//        email.setSubject(subject);
//        email.setText(message);
//        mailSender.send(email);
//        logger.info("{} email sent to: {}", subject, to);
//    }
//}