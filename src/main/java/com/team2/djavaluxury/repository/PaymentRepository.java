package com.team2.djavaluxury.repository;

import com.team2.djavaluxury.constant.TransactionStatus;
import com.team2.djavaluxury.entity.Payment;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.lang.*;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findAllByTransactionStatusIn(Collection<TransactionStatus> transactionStatus);

    @Query("SELECT py FROM Payment py WHERE py.user.id = :userId ORDER BY py.created_at DESC")
    List<Payment> history(@Param("userId") Long userId);
}
