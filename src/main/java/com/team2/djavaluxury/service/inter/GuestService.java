package com.team2.djavaluxury.service.inter;

import com.team2.djavaluxury.dto.response.CommonResponse;

public interface GuestService {
    CommonResponse<String> welcomeMessage();
}
