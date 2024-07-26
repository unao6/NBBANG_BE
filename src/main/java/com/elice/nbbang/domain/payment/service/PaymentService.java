package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import java.util.List;
import java.util.stream.Collectors;
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
        return paymentRepository.findByUserUserIdOrderByPaymentCreatedAtDesc(userId).stream()
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
}
