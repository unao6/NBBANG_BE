package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionApproveRequest;
import com.elice.nbbang.domain.payment.dto.KakaoPaySubscriptionCreateResponse;
import com.elice.nbbang.domain.payment.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
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

    /**
     * 카카오페이 결제준비 생성 API
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/create")
    public ResponseEntity<KakaoPaySubscriptionCreateResponse> createSubscription(@RequestParam Long userId)
        throws Exception {
        KakaoPaySubscriptionCreateResponse response = kakaoPayService.createSubscription(userId);
        log.info("Subscription created: tid={}, nextRedirectPcUrl={}", response.getTid(), response.getNextRedirectPcUrl());
        return ResponseEntity.ok(response);
    }

    /**
     * 카카오페이 결제 승인 API
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/approve")
    public ResponseEntity<KakaoPaySubscriptionCreateResponse> approveSubscription(@RequestBody KakaoPaySubscriptionApproveRequest request) throws Exception {
        log.info("Received approve request: tid={}, pgToken={}", request.getTid(), request.getPgToken());
        kakaoPayService.approveSubscription(request.getTid(), request.getPgToken());
        return ResponseEntity.ok().build();
    }

    /**
     * 카카오페이 결제 취소 API
     * @param request
     * @return
     * @throws Exception
     */
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelPayment(@RequestBody KakaoPayCancelRequest request) throws Exception {
        log.info("Received cancel request: tid={}", request.getTid());
        kakaoPayService.cancelPayment(request);
        return ResponseEntity.ok().build();
    }

    /**
     * 카카오페이 정기결제 신청 API
     * @param userId
     * @return
     * @throws Exception
     */
    @PostMapping("/subscription/{userId}")
    public ResponseEntity<Void> subscription(@PathVariable Long userId, @RequestParam String tid, @RequestParam String sid) throws Exception {
        kakaoPayService.subscription(userId, tid, sid);
        return ResponseEntity.ok().build();
    }
}
