package com.team2.djavaluxury.service.impl;

import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.service.inter.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuestServiceImpl implements GuestService {

    public CommonResponse<String> welcomeMessage() {
        String message = "Welcome to our application! Please register or login to continue.";
        return new CommonResponse<>(200, "Success", message);
    }
}
