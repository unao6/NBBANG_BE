package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateResponse;
import com.elice.nbbang.domain.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/payment/kakaopay")
@RequiredArgsConstructor
@RestController
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;


    @PostMapping("/create")
    public ResponseEntity<KakaoPaySubscriptionCreateResponse> createSubscription(@RequestParam Long userId)
        throws Exception {
        KakaoPaySubscriptionCreateResponse response = kakaoPayService.createSubscription(userId);
        log.info("Subscription created: tid={}, nextRedirectPcUrl={}", response.getTid(), response.getNextRedirectPcUrl());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<KakaoPaySubscriptionCreateResponse> approveSubscription(@RequestBody KakaoPaySubscriptionApproveRequest request) throws Exception {
        log.info("Received approve request: tid={}, pgToken={}", request.getTid(), request.getPgToken());
        kakaoPayService.approveSubscription(request.getTid(), request.getPgToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelPayment(@RequestBody KakaoPayCancelRequest request) throws Exception {
        log.info("Received cancel request: tid={}", request.getTid());
        kakaoPayService.cancelPayment(request);
        return ResponseEntity.ok().build();
    }
}
