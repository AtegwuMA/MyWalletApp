package com.example.mywalletapp.dto.requestdto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasswordRecoveryRequest {
    @NotNull
    private String email;
}
