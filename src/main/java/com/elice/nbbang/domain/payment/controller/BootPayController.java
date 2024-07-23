package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.BootPayTokenResponse;
import com.elice.nbbang.domain.payment.service.BootPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/payment")
@RequiredArgsConstructor
@RestController
public class BootPayController {

    private final BootPayService bootPayService;

    @GetMapping("/token")
    public BootPayTokenResponse getAccessToken() {
        try {
            return bootPayService.getAccessToken();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}