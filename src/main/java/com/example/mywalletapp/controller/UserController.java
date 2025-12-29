package com.example.mywalletapp.controller;

import com.example.mywalletapp.dto.requestdto.*;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    // Endpoint for user registration
    @PostMapping("/signup")
    public ResponseEntity<GenericResponse> signUp(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        GenericResponse response = userService.userSignUp(signUpRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        GenericResponse response = userService.login(loginRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<GenericResponse> requestPasswordReset(@RequestBody PasswordRecoveryRequest request) {
        GenericResponse response = userService.forgotPassword(request);

        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<GenericResponse> resetPassword(
            @RequestParam String token,
            @RequestBody ResetPasswordRequest request) {

        GenericResponse response = userService.resetPassword(token, request);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<GenericResponse> verifyEmail(@RequestParam String token) {
        GenericResponse response = userService.verifyEmail(token);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<GenericResponse> resendVerificationEmail(@RequestParam String email) {
        GenericResponse response = userService.resendVerificationEmail(email);
       return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<GenericResponse> refreshAccessToken(@RequestBody RefreshTokenRequest tokenRequest) {
        GenericResponse response = userService.refreshAccessToken(tokenRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}

