package com.example.mywalletapp.service;

import com.example.mywalletapp.dto.requestdto.AddWalletRequest;
import com.example.mywalletapp.dto.requestdto.TransactionAmount;
import com.example.mywalletapp.dto.requestdto.UpdateWalletTierLimitsRequestDto;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {
    @Transactional
    GenericResponse fundWallet(Long userId, Long walletId, TransactionAmount amount);
    @Transactional
    GenericResponse transferFunds(Long fromUserId, Long fromWalletId, Long toUserId, Long toWalletId, TransactionAmount amount);
    @Transactional
    GenericResponse withdraw(Long userId, Long walletId, TransactionAmount amount);

    GenericResponse getWalletBalance(Long userId, Long walletId);

    GenericResponse addWallet(Long userId, AddWalletRequest newWallet);

    GenericResponse setWalletTierLimits(Long userId, @Valid UpdateWalletTierLimitsRequestDto updateRequest);
}
