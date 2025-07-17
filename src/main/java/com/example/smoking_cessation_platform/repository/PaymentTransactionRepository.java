package com.example.smoking_cessation_platform.repository;

import com.example.smoking_cessation_platform.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Integer>, JpaSpecificationExecutor<PaymentTransaction> {
    Optional<PaymentTransaction> findByTxnRef(String txnRef);

    @Query("SELECT COALESCE(SUM(pt.amount),0) FROM PaymentTransaction pt WHERE pt.status = :status")
    double sumAmountByStatus(String status);

    @Query("SELECT pt.paymentMethod, COALESCE(SUM(pt.amount),0) " +
            "FROM PaymentTransaction pt " +
            "WHERE pt.status = 'SUCCESS' " +
            "GROUP BY pt.paymentMethod")
    List<Object[]> sumRevenueByMethod();

    @Query("SELECT pt.status, COUNT(pt.id) " +
            "FROM PaymentTransaction pt " +
            "GROUP BY pt.status")
    List<Object[]> countTransactionByStatus();
}