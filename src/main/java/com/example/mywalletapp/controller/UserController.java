package com.example.mywalletapp.controller;

import com.example.mywalletapp.dto.requestdto.LoginRequestDto;
import com.example.mywalletapp.dto.requestdto.SignUpRequestDto;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @Autowired
    private UserService userService;

    // Endpoint for user registration
    @PostMapping("/signup")
    public ResponseEntity<GenericResponse> signUp(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        GenericResponse response = userService.signUp(signUpRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // Endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<GenericResponse> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        GenericResponse response = userService.login(loginRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}

