package com.team2.djavaluxury.utils.mapper;

import com.team2.djavaluxury.dto.response.BookingResponse;
import com.team2.djavaluxury.dto.response.PaymentResponse;
import com.team2.djavaluxury.entity.Booking;
import com.team2.djavaluxury.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Named("PaymentMapper")

public interface PaymentMapper {
    PaymentResponse toResponse(Payment payment);
    Payment toEntity(PaymentResponse paymentResponse);
    List<PaymentResponse> toResponseList(List<Payment> payments);
}