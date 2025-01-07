package com.example.mywalletapp.service.serviceimpl;

import com.example.mywalletapp.dto.responsedto.GenericResponse;
import com.example.mywalletapp.exception.CustomException;
import com.example.mywalletapp.model.Transaction;
import com.example.mywalletapp.model.User;
import com.example.mywalletapp.model.Wallet;
import com.example.mywalletapp.repository.TransactionRepository;
import com.example.mywalletapp.repository.UserRepository;
import com.example.mywalletapp.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {


    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    @Override
    public GenericResponse getAllTransactionHistories(Long userId, Pageable pageable) {

        try {
            List<Wallet> wallets = getWalletsByUserId(userId);
            List<Transaction> allTransactions = wallets.stream()
                    .flatMap(wallet -> transactionRepository.findByWallet(wallet, pageable)
                    .getContent().stream())
                    .collect(Collectors.toList());

            logger.info(allTransactions.toString());
            return new GenericResponse(0,
                    "Transaction histories retrieved successfully",
                            HttpStatus.OK, allTransactions
            );

        } catch (Exception e) {
            return new GenericResponse(2,
                    "Failed to retrieve transaction histories: " +
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
            );
        }
    }

    @Override
    public GenericResponse getTransactionHistories(Long userId, Long walletId, Pageable pageable) {
        try {
            Wallet wallet = getWalletByUserIdAndWalletId(userId, walletId);
            List<Transaction> allTransactions = transactionRepository.findByWallet(wallet, pageable)
                    .getContent();

            logger.info(allTransactions.toString());
            return new GenericResponse(0,
                    "Transaction histories retrieved successfully",
                    HttpStatus.OK, allTransactions);
        } catch (Exception e) {
            return new GenericResponse(2,
                    "Failed to retrieve transaction histories: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST);
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
                .orElseThrow(() ->
                        new CustomException("Wallet not found for the specified user"));
    }

}
