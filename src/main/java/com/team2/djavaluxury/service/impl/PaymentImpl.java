package com.team2.djavaluxury.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team2.djavaluxury.constant.TransactionStatus;
import com.team2.djavaluxury.dto.request.PaymentRequest;
import com.team2.djavaluxury.dto.response.PaymentResponse;
import com.team2.djavaluxury.entity.Booking;
import com.team2.djavaluxury.entity.Payment;
import com.team2.djavaluxury.entity.User;
import com.team2.djavaluxury.repository.BookingRepository;
import com.team2.djavaluxury.repository.PaymentRepository;
import com.team2.djavaluxury.repository.UserRepository;
import com.team2.djavaluxury.service.inter.IPaymentService;
import com.team2.djavaluxury.utils.exception.ResourceNotFoundException;
import com.team2.djavaluxury.utils.exception.ValidationException;
import com.team2.djavaluxury.utils.mapper.PaymentMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PaymentImpl implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private final PaymentMapper paymentMapper;
    private final RestClient restClient;
    private final String SECRET_KEY;
    private final String BASE_URL_SNAP;

    @Autowired
    public PaymentImpl(
            PaymentRepository paymentRepository, BookingRepository bookingRepository,
            UserRepository userRepository, PaymentMapper paymentMapper, RestClient restClient,
            @Value("${midtrans.server-key}") String secretKey,
            @Value("${midtrans.api.snap-url}") String baseUrlSnap
    ) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.paymentMapper = paymentMapper;
        this.restClient = restClient;
        SECRET_KEY = secretKey;
        BASE_URL_SNAP = baseUrlSnap;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment createPayment(String bookingId, PaymentRequest paymentRequest) {
        try {
            // Mencari Booking berdasarkan ID
            Booking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ValidationException("Booking not found for ID: " + bookingId, null));

            // Mencari User berdasarkan ID
            User user = userRepository.findById(paymentRequest.getCustomerId())
                    .orElseThrow(() -> new ValidationException("Booking not found for ID: " + bookingId, null));

            // Membuat PaymentRequest
            Map<String, Object> request = Map.of(
                    "transaction_details", Map.of(
                            "order_id", String.valueOf(booking.getId()), // Konversi ke string
//                            "gross_amount", paymentRequest.getPaymentDetail().getAmount()
                            "gross_amount", paymentRequest.getPaymentDetail().getAmount()
                    ),
                    "customer_details", Map.of(
                            "first_name", booking.getUser().getFirstName(),
                            "last_name", booking.getUser().getLastName(),
                            "email", booking.getUser().getEmail()
                    )
            );

            // Mengirim permintaan ke Midtrans
            ResponseEntity<Map<String, String>> response = restClient.post()
                    .uri(BASE_URL_SNAP)
                    .body(request)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()))
                    .retrieve()
                    .toEntity(new ParameterizedTypeReference<>() {
                    });

            Map<String, String> body = response.getBody();
            if (body == null || !body.containsKey("token") || !body.containsKey("redirect_url")) {
                throw new ValidationException("Invalid response from payment provider", null);
            }
            
            // Membuat Payment entity
            Payment payment = Payment.builder()
                    .token(body.get("token"))
                    .redirectUrl(body.get("redirect_url"))
                    .transactionStatus(TransactionStatus.ORDERED)
                    .booking(booking)
                    .user(user)
                    .created_at(Date.from(Instant.now()))
                    .build();

            // Menyimpan Payment ke database
            paymentRepository.saveAndFlush(payment);

            return payment;

        } catch (Exception e) {
            throw new ValidationException("An error occurred while creating payment: " + e.getMessage(), e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void checkFailedAndUpdatePayments() {
        List<TransactionStatus> transactionStatus = List.of(
                TransactionStatus.DENY,
                TransactionStatus.CANCEL,
                TransactionStatus.EXPIRE,
                TransactionStatus.FAILURE
        );
        List<Payment> payments = paymentRepository.findAllByTransactionStatusIn(transactionStatus);
        for (Payment payment : payments) {
            payment.setTransactionStatus(TransactionStatus.FAILURE);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Payment updatePaymentStatus(String paymentId) {
        RestTemplate restTemplate = new RestTemplate();

        try {
            String url = "https://api.sandbox.midtrans.com/v2/" + paymentId + "/status";

            // Mengatur headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));

            // Membuat request dan mengirimkannya
            ResponseEntity<String> response = restTemplate.exchange(
                    UriComponentsBuilder.fromHttpUrl(url).toUriString(),
                    HttpMethod.GET,
                    new org.springframework.http.HttpEntity<>(headers),
                    String.class
            );
            System.out.println("ini line 157 :" + response.getBody());
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new ValidationException("Payment not found for ID: " + paymentId, null));

            // Parse response body
            String responseBody = response.getBody();
            if (responseBody != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);

                // Extract transaction_status from Midtrans API response
                String transactionStatus = rootNode.path("transaction_status").asText();
                System.out.println("transaction status : " + transactionStatus);
                // Update Payment transaction status
                try {
                    payment.setTransactionStatus(TransactionStatus.valueOf(transactionStatus.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid transaction status: " + transactionStatus, e);
                }

                // Save updated Payment entity
                return paymentRepository.save(payment);
            } else {
                throw new ValidationException("Empty response from Midtrans API", null);
            }
        } catch (HttpClientErrorException e) {
            throw new ValidationException("HTTP Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new ValidationException("An error occurred while updating payment status: " + e.getMessage(), e);
        }
    }

    @Override
    public List<PaymentResponse> findAll() {
        // Ambil semua data pembayaran dan ubah ke PaymentResponse
        List<PaymentResponse> payment = paymentRepository.findAll().stream()
                .map(paymentMapper::toResponse)
                .collect(Collectors.toList());


        if (payment.isEmpty()) {
            throw new ResourceNotFoundException("No transaction yet !", null);
        }

        return payment;
    }

    @Override
    public List<PaymentResponse> getPaymentHistory(Long userId) {
        // Ambil data payment berdasarkan userId
        List<Payment> payments = paymentRepository.history(userId);

        // Jika tidak ada transaksi, lemparkan exception
        if (payments.isEmpty()) {
            throw new ResourceNotFoundException("You don't have any transaction yet!", null);
        }

        // Map daftar Payment ke daftar PaymentResponse menggunakan mapper
        return paymentMapper.toResponseList(payments);
    }

}
