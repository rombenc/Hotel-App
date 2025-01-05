package com.team2.djavaluxury.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentCustomerRequest {
    @JsonProperty("name")
    private String name;
}
