package com.team2.djavaluxury.controller;

import com.team2.djavaluxury.dto.response.CommonResponse;
import com.team2.djavaluxury.service.impl.GuestServiceImpl;
import com.team2.djavaluxury.service.inter.GuestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/guest")
@RestController
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping("/welcome")
    public ResponseEntity<CommonResponse<String>> welcomeMessage() {
        return ResponseEntity.ok(guestService.welcomeMessage());
    }
}
