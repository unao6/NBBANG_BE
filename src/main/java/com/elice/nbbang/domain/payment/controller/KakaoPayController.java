package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateResponse;
import com.elice.nbbang.domain.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/payment/kakaopay")
@RequiredArgsConstructor
@RestController
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @PostMapping("/create")
    public ResponseEntity<KakaoPaySubscriptionCreateResponse> createSubscription(@RequestParam Long userId)
        throws Exception {
        KakaoPaySubscriptionCreateResponse response = kakaoPayService.createSubscription(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<KakaoPaySubscriptionCreateResponse> approveSubscription(@RequestBody KakaoPaySubscriptionApproveRequest request)
        throws Exception {
        KakaoPaySubscriptionCreateResponse response = kakaoPayService.approveSubscription(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/success")
    public ResponseEntity<String> paymentSuccess(@RequestParam("pg_token") String pgToken) {
        // Handle payment success logic
        return ResponseEntity.ok("Payment Success! PG Token: " + pgToken);
    }

    @GetMapping("/fail")
    public ResponseEntity<String> paymentFail() {
        // Handle payment failure logic
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment Failed!");
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> paymentCancel() {
        // Handle payment cancellation logic
        return ResponseEntity.ok("Payment Cancelled!");
    }
}
