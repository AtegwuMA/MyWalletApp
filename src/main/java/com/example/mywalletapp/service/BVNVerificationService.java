package com.example.mywalletapp.service;

public interface BVNVerificationService {

    boolean verifyBVN(String bvn, String surname, String dateOfBirth);
}
