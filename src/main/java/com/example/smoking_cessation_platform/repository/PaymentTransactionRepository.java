package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer>, JpaSpecificationExecutor<PaymentTransaction> {
    Optional<PaymentTransaction> findByTxnRef(String txnRef);
}