package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.request.PaymentRequest;
import com.team2.djavaluxury.dto.response.PaymentResponse;
import com.team2.djavaluxury.entity.Payment;

import java.util.*;

public interface IPaymentService {
    Payment createPayment(String bookingId, PaymentRequest request);

    Payment updatePaymentStatus(String Payment_id);

    void checkFailedAndUpdatePayments();

    List<PaymentResponse> findAll();

    List<PaymentResponse> getPaymentHistory(Long userId);
}
