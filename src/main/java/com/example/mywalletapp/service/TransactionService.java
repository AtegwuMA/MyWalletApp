package com.example.mywalletapp.service;

import com.example.mywalletapp.dto.responsedto.GenericResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {
    GenericResponse getAllTransactionHistories(Long userId, Pageable pageable);

    GenericResponse getTransactionHistories(Long userId, Long walletId, Pageable pageable);
}
