package com.example.mywalletapp.controller;


import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.service.TransactionService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<GenericResponse> getAllTransactionHistories(
            @PathVariable @Positive Long userId,
            @PageableDefault Pageable pageable) {
        GenericResponse response = transactionService.getAllTransactionHistories(userId,
                                                    pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/user/{userId}/wallet/{walletId}")
    public ResponseEntity<GenericResponse> getTransactionHistories(
            @PathVariable @Positive Long userId,
            @PathVariable @Positive Long walletId,
            @PageableDefault Pageable pageable) {
        GenericResponse response = transactionService.getTransactionHistories(userId,
                                            walletId, pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
}
