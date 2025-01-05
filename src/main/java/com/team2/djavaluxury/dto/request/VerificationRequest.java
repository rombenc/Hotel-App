package com.team2.djavaluxury.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    private String email;
    private String verificationCode;
}
