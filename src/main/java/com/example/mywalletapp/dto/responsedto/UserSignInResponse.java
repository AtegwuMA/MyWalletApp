package com.example.mywalletapp.dto.responsedto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignInResponse {
    private String email;
    private Long id;
    private String token;
    private String type;
    private List<String> roles;
}
