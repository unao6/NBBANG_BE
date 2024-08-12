package com.elice.nbbang.domain.payment.controller;

import static com.elice.nbbang.global.exception.ErrorCode.NOT_FOUND_PARTY;

import com.elice.nbbang.domain.party.entity.Party;
import com.elice.nbbang.domain.party.repository.PartyRepository;
import com.elice.nbbang.domain.payment.dto.KakaoPayCancelRequest;
import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.dto.PaymentRefundDTO;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.service.PaymentService;
import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    private final PartyRepository partyRepository;
    private final UserUtil userUtil;

    /**
     * 모든 Payments 조회 (페이지네이션 적용)
     */
    @GetMapping("/list")
    public ResponseEntity<Page<PaymentDto>> getPayments(
        @PageableDefault(size = 10) Pageable pageable) {
        Page<PaymentDto> payments = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * userId로 Payments 조회
     */
    @GetMapping("/list/user/{partner_user_id}")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByPartnerUserId(
        @PathVariable("partner_user_id") String partnerUserId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDto> payments = paymentService.getPaymentsByPartnerUserId(partnerUserId, pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * TID로 Payments 조회
     */
    @GetMapping("/list/tid/{tid}")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByTid(
        @PathVariable("tid") String tid,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDto> payments = paymentService.getPaymentsByTid(tid, pageable);
        return ResponseEntity.ok(payments);
    }

    /**
     * PaymentStatus 상태별 Payments 조회
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Page<PaymentDto>> getPaymentsByStatus(
        @PathVariable PaymentStatus status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentDto> payments = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(payments);
    }


    /**
     * 환불 정보 조회
     */
    @GetMapping("/refund/{partyId}/info")
    public ResponseEntity<PaymentRefundDTO> getRefundInfo(@PathVariable Long partyId) {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = paymentService.getAuthenticatedUserId();

        // partyId를 통해 Party 엔티티를 조회
        Party party = partyRepository.findById(partyId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PARTY));

        // Party 엔티티에서 ottId를 가져옴
        Long ottId = party.getOtt().getId();

        PaymentRefundDTO paymentRefundDTO = paymentService.getRefundInfo(userId, ottId);
        return ResponseEntity.ok(paymentRefundDTO);
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
