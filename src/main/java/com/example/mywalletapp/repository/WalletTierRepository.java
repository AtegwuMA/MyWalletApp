package com.example.mywalletapp.repository;


import com.example.mywalletapp.model.WalletTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTierRepository extends JpaRepository<WalletTier, Long> {
    WalletTier findByName(String name);
}
