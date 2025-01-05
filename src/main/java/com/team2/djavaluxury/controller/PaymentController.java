package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.dto.request.PaymentRequest;
import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.dto.response.PaymentResponse;
import com.team2.djavaluxury.entity.Payment;
import com.team2.djavaluxury.service.inter.IPaymentService;
import com.team2.djavaluxury.utils.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.lang.*;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    // Endpoint untuk mendapatkan semua pembayaran
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<CommonResponse<List<PaymentResponse>>> getAllPayment() {
        List<PaymentResponse> paymentResponse = paymentService.findAll();

        if (paymentResponse.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)  // 204 No Content jika tidak ada data
                    .body(CommonResponse.<List<PaymentResponse>>builder()
                            .statusCode(HttpStatus.NO_CONTENT.value())
                            .message("No Data Found!")
                            .data(paymentResponse)
                            .build());
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(CommonResponse.<List<PaymentResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Data Found!")
                        .data(paymentResponse)
                        .build());
    }

    // Endpoint untuk mendapatkan riwayat pembayaran berdasarkan userId
    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<CommonResponse<List<PaymentResponse>>> getPaymentHistory(@PathVariable Long userId) {
        List<PaymentResponse> paymentResponse = paymentService.getPaymentHistory(userId);

        HttpStatus status = paymentResponse.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK;
        String message = paymentResponse.isEmpty() ? "History not Found!" : "Data Found!";

        return ResponseEntity
                .status(status)
                .body(CommonResponse.<List<PaymentResponse>>builder()
                        .statusCode(status.value())
                        .message(message)
                        .data(paymentResponse)
                        .build());
    }

    // Endpoint untuk membuat pembayaran baru
    @PostMapping("/{bookingId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USER')")
    public ResponseEntity<CommonResponse<Payment>> createPayment(@PathVariable String bookingId, @RequestBody PaymentRequest request) {
        Payment paymentResponse = paymentService.createPayment(bookingId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED) // 201 Created jika sukses
                .body(CommonResponse.<Payment>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Payment created successfully")
                        .data(paymentResponse)
                        .build());
    }

    // Endpoint untuk memperbarui status pembayaran
    @PutMapping("/{paymentId}")
    public ResponseEntity<CommonResponse<PaymentResponse>> updatePaymentStatus(@PathVariable("paymentId") String paymentId) {
        try {
            // Perbarui status pembayaran
            Payment updatedPayment = paymentService.updatePaymentStatus(paymentId);

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setId(updatedPayment.getId());
            paymentResponse.setTransaction_status(updatedPayment.getTransactionStatus().getName());
            paymentResponse.setUpdatedAt(updatedPayment.getUpdatedAt());

            return ResponseEntity.ok(CommonResponse.<PaymentResponse>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Payment status updated successfully")
                    .data(paymentResponse)
                    .build());
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(CommonResponse.<PaymentResponse>builder()
                    .statusCode(HttpStatus.BAD_REQUEST.value())
                    .message("Validation Error: " + e.getMessage())
                    .data(null)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CommonResponse.<PaymentResponse>builder()
                    .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An error occurred: " + e.getMessage())
                    .data(null)
                    .build());
        }
    }
}

