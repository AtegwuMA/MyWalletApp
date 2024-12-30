package com.example.mywalletapp.repository;


import com.example.mywalletapp.model.Transaction;
import com.example.mywalletapp.model.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);
    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);
}
