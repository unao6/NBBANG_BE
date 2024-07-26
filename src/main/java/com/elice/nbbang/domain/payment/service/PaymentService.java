package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.PaymentRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment createPayment(PaymentRegisterDTO registerDTO) {
        Payment payment = Payment.builder()
            .billingKey(registerDTO.getBillingKey())
            .amount(registerDTO.getAmount())
            .paymentDate(registerDTO.getPaymentDate())
            .paymentType(registerDTO.getPaymentType())
            .paymentStatus(registerDTO.getPaymentStatus())
            .reserveId(registerDTO.getReserveId())
            .build();
        paymentRepository.save(payment);
        return payment;
    }

    public void deletePayment(String id) {
        Payment payment = paymentRepository.findByReserveId(id);
        Payment updatedPayment = payment.toBuilder()
            .paymentStatus(PaymentStatus.RESERVE_CANCELLED)
            .build();
        paymentRepository.save(updatedPayment);
    }
}
