package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import java.util.List;
import java.util.stream.Collectors;
import com.elice.nbbang.domain.payment.dto.PaymentRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * 모든 Payments 조회
     */
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAllByOrderByPaymentCreatedAtDesc().stream()
            .map(PaymentDto::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * userId로 Payments 조회
     */
    public List<PaymentDto> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserIdOrderByPaymentCreatedAtDesc(userId).stream()
            .map(PaymentDto::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * 상태별 Payments 조회
     */
    public List<PaymentDto> getPaymentsByStatus(PaymentStatus status) {
        return paymentRepository.findByStatusOrderByPaymentCreatedAtDesc(status).stream()
            .map(PaymentDto::fromEntity)
            .collect(Collectors.toList());
    }

    //payment 생성
    @Transactional(readOnly = false)
    public Payment createPayment(PaymentRegisterDTO registerDTO) {
        Payment payment = Payment.builder()
            .billingKey(registerDTO.getBillingKey())
            .amount(registerDTO.getAmount())
            .paymentSubscribedAt(registerDTO.getPaymentSubscribedAt())
            .paymentType(registerDTO.getPaymentType())
            .status(registerDTO.getPaymentStatus())
            .reserveId(registerDTO.getReserveId())
            .build();
        paymentRepository.save(payment);
        return payment;
    }

    //payment 취소
    @Transactional(readOnly = false)
    public void deletePayment(String id) {
        Payment payment = paymentRepository.findByReserveId(id);
        Payment updatedPayment = payment.toBuilder()
            .status(PaymentStatus.RESERVE_CANCELLED)
            .build();
        paymentRepository.save(updatedPayment);
    }
}
