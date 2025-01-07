package com.example.mywalletapp.controller;

import com.example.mywalletapp.dto.requestdto.SignUpAdminRequestDto;
import com.example.mywalletapp.dto.requestdto.UpdateWalletTierLimitsRequestDto;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.service.UserService;
import com.example.mywalletapp.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/api/admin")
public class AdminController {


    private final UserService userService;

    private final WalletService walletService;

    @PreAuthorize("hasRole('ADMIN')") // Require admin role to access this endpoint
    @PostMapping("/create")
    public ResponseEntity<GenericResponse> createAdmin(@Valid @RequestBody SignUpAdminRequestDto signUpRequest) {
        GenericResponse response = userService.adminSignUp(signUpRequest); // Use the existing sign-up logic
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/set-wallet-tier/{userId}")
    public ResponseEntity<GenericResponse> setWalletTier(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateWalletTierLimitsRequestDto updateRequest) {
        updateRequest.setUserId(userId); // Set userId from the path variable
        GenericResponse response = walletService.setWalletTierLimits(userId, updateRequest);
        return new ResponseEntity<>(response, response.getHttpStatus());
    }

}