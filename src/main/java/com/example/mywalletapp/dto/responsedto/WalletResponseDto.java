package com.example.mywalletapp.dto.responsedto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class WalletResponseDto {
    private Long id;
    private String tier;
    private Double balance;

    // Getters and setters
}
