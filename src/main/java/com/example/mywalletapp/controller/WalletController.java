package com.example.mywalletapp.controller;

import com.example.mywalletapp.dto.requestdto.AddWalletRequest;
import com.example.mywalletapp.dto.requestdto.TransactionAmount;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/{userId}/fund/{walletId}")
    public ResponseEntity<GenericResponse> fundWallet(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestBody TransactionAmount amount) {
        GenericResponse response = walletService.fundWallet(userId, walletId, amount);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/{fromUserId}/transfer/{fromWalletId}/to/{toUserId}/wallet/{toWalletId}")
    public ResponseEntity<GenericResponse> transferFunds(
            @PathVariable Long fromUserId,
            @PathVariable Long fromWalletId,
            @PathVariable Long toUserId,
            @PathVariable Long toWalletId,
            @RequestBody TransactionAmount amount) {
        GenericResponse response = walletService.transferFunds(fromUserId, fromWalletId,
                                                        toUserId, toWalletId, amount);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PostMapping("/{userId}/withdraw/{walletId}")
    public ResponseEntity<GenericResponse> withdraw(
            @PathVariable Long userId,
            @PathVariable Long walletId,
            @RequestBody TransactionAmount amount) {
        GenericResponse response = walletService.withdraw(userId, walletId, amount);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }


    @PostMapping("/{userId}/add")
    public ResponseEntity<GenericResponse> addWallet(
            @PathVariable Long userId,
            @RequestBody AddWalletRequest newWallet) {
        GenericResponse response = walletService.addWallet(userId, newWallet);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @GetMapping("/{userId}/balance/{walletId}")
    public ResponseEntity<GenericResponse> getWalletBalance(@PathVariable Long userId,
                                                            @PathVariable Long walletId
    ) {
        GenericResponse response = walletService.getWalletBalance(userId, walletId);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}