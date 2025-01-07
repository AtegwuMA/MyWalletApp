package com.example.mywalletapp.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWalletTierLimitsRequestDto {
    private Long userId;
    private String tierName; // The name of the tier to update
    private Double dailyFundingLimit; // New daily funding limit
    private Double weeklyFundingLimit; // New weekly funding limit
    private Double dailyTransferLimit; // New daily transfer limit
    private Double weeklyTransferLimit; // New weekly transfer limit
    private Double dailyWithdrawLimit; // New daily withdraw limit
}