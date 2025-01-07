package com.example.mywalletapp.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);

    void sendPasswordResetEmail(String email, String token);
}
