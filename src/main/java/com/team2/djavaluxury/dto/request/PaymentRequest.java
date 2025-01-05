package com.team2.djavaluxury.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.team2.djavaluxury.entity.Room;
import com.team2.djavaluxury.entity.User;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentRequest {
    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty("transaction_details")
    private PaymentDetailRequest paymentDetail;

}
