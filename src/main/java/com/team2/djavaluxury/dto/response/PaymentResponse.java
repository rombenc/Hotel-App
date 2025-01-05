package com.team2.djavaluxury.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team2.djavaluxury.entity.Room;
import com.team2.djavaluxury.entity.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {
    private String id;
    private String redirect_url;
    private String token;
    private String transaction_status;
    private User user;
    private LocalDateTime updatedAt;
}
