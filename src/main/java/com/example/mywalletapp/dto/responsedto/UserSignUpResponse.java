package com.example.mywalletapp.dto.responsedto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Builder
public class UserSignUpResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String dateOfBirth;
    private String address;
    private String bvn;
    private LocalDateTime createdAt;
    private List<WalletResponseDto> wallets;

}
