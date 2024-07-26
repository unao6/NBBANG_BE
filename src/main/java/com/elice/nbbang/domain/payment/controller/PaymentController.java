package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.service.PaymentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/payment")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 모든 Payments 조회
     */
    @GetMapping("/list")
    public ResponseEntity<List<PaymentDto>> getPayments() {
        List<PaymentDto> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * userId로 Payments 조회
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<PaymentDto>> getUserPayments(@PathVariable Long userId) {
        List<PaymentDto> userPayments = paymentService.getPaymentsByUserId(userId);
        return ResponseEntity.ok(userPayments);
    }

    /**
     * PaymentStatus 상태별 Payments 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDto>> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        List<PaymentDto> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    /**
     * PaymentId로 결제 취소 요청
     */
    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Void> requestRefund(
        @PathVariable Long paymentId,
        @RequestBody KakaoPayCancelRequest request) {
        return ResponseEntity.ok().build();
    }

}
