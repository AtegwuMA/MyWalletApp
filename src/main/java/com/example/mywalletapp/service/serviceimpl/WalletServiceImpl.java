package com.example.mywalletapp.service.serviceimpl;

//public class WalletServiceImpl {

import com.example.mywalletapp.dto.requestdto.AddWalletRequest;
import com.example.mywalletapp.dto.requestdto.TransactionAmount;
import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.exception.CustomException;
import com.example.mywalletapp.model.Transaction;
import com.example.mywalletapp.model.User;
import com.example.mywalletapp.model.Wallet;
import com.example.mywalletapp.model.WalletTier;
import com.example.mywalletapp.repository.*;
import com.example.mywalletapp.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class WalletServiceImpl implements WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletTierRepository walletTierRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public GenericResponse fundWallet(Long userId, Long walletId, TransactionAmount transaction) {
        if (userId == null || walletId == null || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        try {
            Wallet wallet = getWallet(userId, walletId);
            validateFundingLimits(wallet, transaction.getAmount(), "fund");

            wallet.setBalance(wallet.getBalance() + transaction.getAmount());
            walletRepository.save(wallet);

            // Add transaction record
            recordTransaction(wallet, transaction.getAmount(), "Credit");

            updateFundingStatistics(wallet, transaction.getAmount(), "fund");

            return new GenericResponse(00, "Wallet funded successfully", HttpStatus.OK, null);
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            return new GenericResponse(02, "Failed to fund wallet", HttpStatus.BAD_REQUEST, null);
        }
    }

    @Override
    public GenericResponse transferFunds(Long fromUserId, Long fromWalletId, Long toUserId, Long toWalletId, TransactionAmount transaction) {
        if (fromUserId == null || fromWalletId == null || toUserId == null || toWalletId == null || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        try {
            Wallet fromWallet = getWallet(fromUserId, fromWalletId);
            Wallet toWallet = getWallet(toUserId, toWalletId);

            validateFundingLimits(fromWallet, transaction.getAmount(), "transfer");

            if (fromWallet.getBalance() < transaction.getAmount()) {
                throw new CustomException("Insufficient balance");
            }

            fromWallet.setBalance(fromWallet.getBalance() - transaction.getAmount());
            toWallet.setBalance(toWallet.getBalance() + transaction.getAmount());
            walletRepository.save(fromWallet);
            walletRepository.save(toWallet);

            // Add transaction records
            recordTransaction(fromWallet, transaction.getAmount(), "Debit");
            recordTransaction(toWallet, transaction.getAmount(), "Credit");

            updateFundingStatistics(fromWallet, transaction.getAmount(), "transfer");

            return new GenericResponse(00, "Funds transferred successfully", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            return new GenericResponse(02, "Failed to transfer funds", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public GenericResponse withdraw(Long userId, Long walletId, TransactionAmount transaction) {
        if (userId == null || walletId == null || transaction.getAmount() <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        try {
            Wallet wallet = getWallet(userId, walletId);

            validateFundingLimits(wallet, transaction.getAmount(), "withdraw");

            if (wallet.getBalance() <transaction.getAmount()) {
                throw new CustomException("Insufficient balance");
            }

            wallet.setBalance(wallet.getBalance() - transaction.getAmount());
            walletRepository.save(wallet);

            // Add transaction record
            recordTransaction(wallet,transaction.getAmount(), "Debit");

            updateFundingStatistics(wallet, transaction.getAmount(), "withdraw");

            return new GenericResponse(00, "Withdrawal successful", HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception occurred: ", e);
            return new GenericResponse(02, "Failed to withdraw funds", HttpStatus.BAD_REQUEST);
        }
    }


    public GenericResponse getWalletBalance(Long userId, Long walletId) {
        try {
            Wallet wallet = getWalletByUserIdAndWalletId(userId, walletId);
            double balance = wallet.getBalance();
            return new GenericResponse(00, "Wallet balance retrieved successfully", HttpStatus.OK, balance);
        } catch (Exception e) {
            return new GenericResponse(02, "Failed to retrieve wallet balance: " + e.getMessage(), HttpStatus.BAD_REQUEST, null);
        }
    }
    public GenericResponse addWallet(Long userId, AddWalletRequest wallet) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException("User not found"));

            // Set up the new wallet
            Wallet newWallet = new Wallet();
            newWallet.setTier(wallet.getTier());
            newWallet.setBalance(0.0);
            newWallet.setUser(user); // Associate the new wallet with the user
            walletRepository.save(newWallet); // Save the new wallet to the database

            return new GenericResponse(00, "Wallet added successfully", HttpStatus.CREATED, newWallet);
        } catch (Exception e) {
            return new GenericResponse(02, "Failed to add wallet: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public GenericResponse getAllTransactionHistories(Long userId, Pageable pageable) {
        try {
            List<Wallet> wallets = getWalletsByUserId(userId);
            List<Transaction> allTransactions = wallets.stream()
                    .flatMap(wallet -> transactionRepository.findByWallet(wallet, pageable).getContent().stream())
                    .collect(Collectors.toList());
            return new GenericResponse(00, "Transaction histories retrieved successfully", HttpStatus.OK, allTransactions);
        } catch (Exception e) {
            return new GenericResponse(02, "Failed to retrieve transaction histories: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private List<Wallet> getWalletsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));
        return user.getWallets(); // Assuming this returns a List<Wallet>
    }

    private Wallet getWalletByUserIdAndWalletId(Long userId, Long walletId) {
        List<Wallet> wallets = getWalletsByUserId(userId);
        return wallets.stream()
                .filter(wallet -> wallet.getId().equals(walletId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Wallet not found for the specified user"));
    }


    // Other methods remain unchanged...
        private Wallet getWallet(Long userId, Long walletId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException("User not found"));
        return user.getWallets().stream()
                .filter(wallet -> wallet.getId().equals(walletId))
                .findFirst()
                .orElseThrow(() -> new CustomException("Wallet not found"));
    }

    private void validateFundingLimits(Wallet wallet, double amount, String type) {
        WalletTier tier = walletTierRepository.findByName(wallet.getTier());

        switch (type) {
            case "fund":
                // Check daily limit
                if (wallet.getDailyFundingAmount() + amount > tier.getDailyFundingLimit()) {
                    throw new CustomException("Exceeds daily funding limit");
                }
                // Check weekly limit
                if (wallet.getWeeklyFundingAmount() + amount > tier.getWeeklyFundingLimit()) {
                    throw new CustomException("Exceeds weekly funding limit");
                }
                break;
            case "transfer":
                // Check daily limit
                if (wallet.getDailyTransferAmount() + amount > tier.getDailyTransferLimit()) {
                    throw new CustomException("Exceeds daily transfer limit");
                }
                // Check weekly limit
                if (wallet.getWeeklyTransferAmount() + amount > tier.getWeeklyTransferLimit()) {
                    throw new CustomException("Exceeds weekly transfer limit");
                }
                break;
            case "withdraw":
                // Check daily limit
                if (wallet.getDailyWithdrawAmount() + amount > tier.getDailyWithdrawLimit()) {
                    throw new CustomException("Exceeds daily withdraw limit");
                }
                // Check weekly limit
                if (wallet.getWeeklyWithdrawAmount() + amount > tier.getWeeklyWithdrawLimit()) {
                    throw new CustomException("Exceeds weekly withdraw limit");
                }
                break;
        }
    }

    private void updateFundingStatistics(Wallet wallet, double amount, String type) {
        LocalDateTime now = LocalDateTime.now();

        // Reset daily limit at start of new day
        if (wallet.getLastFundingTime() == null || wallet.getLastFundingTime().toLocalDate().isBefore(now.toLocalDate())) {
            wallet.setDailyFundingAmount(0.0);
        }
        // Reset weekly limit at start of new week
        if (wallet.getLastFundingTime() == null || wallet.getLastFundingTime().toLocalDate().getDayOfWeek().getValue() == 1 &&
                now.getDayOfWeek().getValue() != 1) {
            wallet.setWeeklyFundingAmount(0.0);
        }

        switch (type) {
            case "fund":
                wallet.setDailyFundingAmount(wallet.getDailyFundingAmount() + amount);
                wallet.setWeeklyFundingAmount(wallet.getWeeklyFundingAmount() + amount);
                wallet.setLastFundingTime(now);
                break;
            case "transfer":
                wallet.setDailyTransferAmount(wallet.getDailyTransferAmount() + amount);
                wallet.setWeeklyTransferAmount(wallet.getWeeklyTransferAmount() + amount);
                wallet.setLastTransferFundTime(now);
                break;
            case "withdraw":
                wallet.setDailyWithdrawAmount(wallet.getDailyWithdrawAmount() + amount);
                wallet.setWeeklyWithdrawAmount(wallet.getWeeklyWithdrawAmount() + amount);
                wallet.setLastWithdrawTime(now);
                break;
        }

        walletRepository.save(wallet);
    }

    private void recordTransaction(Wallet wallet, double amount, String type) {
        Transaction transaction = Transaction.builder()
                .amount(amount)
                .type(type)
                .timestamp(LocalDateTime.now())
                .wallet(wallet)
                .build();
        transactionRepository.save(transaction); // Assuming you have a TransactionRepository
    }


    public void setWalletTier(Long userId, WalletTier walletTier, String tierName) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException("User not found"));
//        // Check if the user has admin role
//        if (!user.getRole().equals("admin")) {
//            throw new CustomException("Unauthorized: User must be an admin.");
//        }

        // Validate the tierName
        if (!tierName.equals("BASIC") && !tierName.equals("SILVER") && !tierName.equals("GOLD")) {
            throw new CustomException("Invalid tier name: " + tierName + ". Allowed values are: BASIC, SILVER, GOLD.");
        }

        // Find the wallet tier by name
        WalletTier tier = walletTierRepository.findByName(tierName);
        if (tier == null) {
            throw new CustomException("WalletTier not found for the name: " + tierName);
        }

        // Update the wallet tier limits
        tier.setDailyFundingLimit(walletTier.getDailyFundingLimit());
        tier.setWeeklyFundingLimit(walletTier.getWeeklyFundingLimit());
        tier.setDailyTransferLimit(walletTier.getDailyTransferLimit());
        tier.setWeeklyTransferLimit(walletTier.getWeeklyTransferLimit());
        tier.setDailyWithdrawLimit(walletTier.getDailyWithdrawLimit());
        tier.setWeeklyWithdrawLimit(walletTier.getWeeklyWithdrawLimit());

        // Save the updated tier
        walletTierRepository.save(tier);
    }

}
