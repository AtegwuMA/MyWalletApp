package com.example.mywalletapp.controller;

import com.example.mywalletapp.dto.requestdto.AddWalletRequest;
import com.example.mywalletapp.dto.requestdto.TransactionAmount;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @PostMapping("/{userId}/fund/{walletId}")
    public ResponseEntity<GenericResponse> fundWallet(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestParam TransactionAmount amount) {
        GenericResponse response = walletService.fundWallet(userId, walletId, amount);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/{fromUserId}/transfer/{fromWalletId}/to/{toUserId}/wallet/{toWalletId}")
    public ResponseEntity<GenericResponse> transferFunds(
            @PathVariable Long fromUserId,
            @PathVariable Long fromWalletId,
            @PathVariable Long toUserId,
            @PathVariable Long toWalletId,
            @RequestParam TransactionAmount amount) {
        GenericResponse response = walletService.transferFunds(fromUserId, fromWalletId, toUserId, toWalletId, amount);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/{userId}/withdraw/{walletId}")
    public ResponseEntity<GenericResponse> withdraw(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestParam TransactionAmount amount) {
        GenericResponse response = walletService.withdraw(userId, walletId, amount);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    // Add additional methods as needed for other wallet operations...

    @PostMapping("/{userId}/add")
    public ResponseEntity<GenericResponse> addWallet(
            @PathVariable Long userId,
            @RequestBody AddWalletRequest newWallet) {
        GenericResponse response = walletService.addWallet(userId, newWallet);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }
    @GetMapping("/{userId}/balance/{walletId}")
    public ResponseEntity<GenericResponse> getWalletBalance(@PathVariable Long userId, @PathVariable Long walletId) {
        GenericResponse response = walletService.getWalletBalance(userId, walletId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/{userId}/transactions")
    public ResponseEntity<GenericResponse> getAllTransactionHistories(
            @PathVariable Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        GenericResponse response = walletService.getAllTransactionHistories(userId, pageable);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }


}