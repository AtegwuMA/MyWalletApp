package com.example.mywalletapp.service;



import com.example.mywalletapp.dto.requestdto.AddWalletRequest;
import com.example.mywalletapp.dto.requestdto.TransactionAmount;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface WalletService {
    GenericResponse fundWallet(Long userId, Long walletId, TransactionAmount amount);

    GenericResponse transferFunds(Long fromUserId, Long fromWalletId, Long toUserId, Long toWalletId, TransactionAmount amount);

    GenericResponse withdraw(Long userId, Long walletId, TransactionAmount amount);

    GenericResponse getWalletBalance(Long userId, Long walletId);

    GenericResponse getAllTransactionHistories(Long userId, Pageable pageable);

    GenericResponse addWallet(Long userId, AddWalletRequest newWallet);
}
