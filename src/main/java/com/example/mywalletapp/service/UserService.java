package com.example.mywalletapp.service;


import com.example.mywalletapp.dto.requestdto.*;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    GenericResponse userSignUp(SignUpRequestDto signUpRequest);

    GenericResponse login(LoginRequestDto loginRequest);

    GenericResponse adminSignUp(@Valid SignUpAdminRequestDto signUpRequest);

    GenericResponse refreshAccessToken(RefreshTokenRequest tokenRequest);

    @Transactional
    GenericResponse verifyEmail(String token);

    GenericResponse resendVerificationEmail(String email);

    GenericResponse forgotPassword(PasswordRecoveryRequest request);

    @Transactional
    GenericResponse resetPassword(String token, ResetPasswordRequest request);
}
