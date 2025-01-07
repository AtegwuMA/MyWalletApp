package com.example.mywalletapp.model;


import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class WalletTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name; // e.g., BASIC, SILVER, GOLD
    private double dailyFundingLimit;
    private double weeklyFundingLimit;
    private double dailyTransferLimit;
    private double weeklyTransferLimit;
    private double dailyWithdrawLimit;
    private double weeklyWithdrawLimit;

    // Getters and Setters
}
