package com.example.mywalletapp.service;


import com.example.mywalletapp.dto.requestdto.LoginRequestDto;
import com.example.mywalletapp.dto.requestdto.SignUpRequestDto;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    GenericResponse signUp(SignUpRequestDto signUpRequest);

    GenericResponse login(LoginRequestDto loginRequest);
}
