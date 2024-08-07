package com.elice.nbbang.domain.payment.controller;

import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.service.PaymentService;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.domain.user.service.UserService;
import com.elice.nbbang.global.util.UserUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/api/payment")
@RequiredArgsConstructor
@RestController
public class PaymentController {

    private final PaymentService paymentService;
    private final UserUtil userUtil;

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
    @GetMapping("/list/")
    public ResponseEntity<List<PaymentDto>> getUserPayments() {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = paymentService.getAuthenticatedUserId();


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

    /**
     * 환불 신청시 호출되는 API
     * @param ottId + userId
     */
    @PostMapping("/refund/{ottId}")
    public ResponseEntity<Void> changeRefundStatus(@PathVariable Long ottId) {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Long userId = paymentService.getAuthenticatedUserId();

        paymentService.getRefundAmount(userId, ottId);
        return ResponseEntity.ok().build();
    }
}
