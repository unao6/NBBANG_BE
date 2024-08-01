package com.elice.nbbang.domain.payment.service;

import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.elice.nbbang.domain.payment.dto.PaymentRegisterDTO;
import com.elice.nbbang.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BootPayService bootPayService;

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
    public Payment createPayment(PaymentReserve reserve, String reserveId) {
        Payment payment = Payment.builder()
            .billingKey(reserve.getBillingKey())
            .amount(reserve.getAmount())
            .paymentSubscribedAt(reserve.getPaymentSubscribedAt())
            .paymentType(PaymentType.CARD)
            .status(PaymentStatus.RESERVE_COMPLETED)
            .reserveId(reserveId)
            .build();
        paymentRepository.save(payment);
        return payment;
    }

    //payment 취소
    @Transactional(readOnly = false)
    public void deletePayment(String id) {
        Payment payment = paymentRepository.findByReserveId(id).orElse(null);
        Payment updatedPayment = payment.toBuilder()
            .status(PaymentStatus.RESERVE_CANCELLED)
            .build();
        paymentRepository.save(updatedPayment);
    }

    //payment 조회
    public Payment getPaymentByReserveId(String id) {
        return paymentRepository.findByReserveId(id).orElse(null);
    }

    //예약완료된 payment를 List로 반환
    public List<String> getAllReservedPayment() {
        List<Payment> paymentList = paymentRepository.findAllByStatus(PaymentStatus.RESERVE_COMPLETED);

        List<String> reserveIds = paymentList.stream()
                                            .map(Payment::getReserveId)
                                            .collect(Collectors.toList());

        return reserveIds;
    }

    //예약완료된 payment를 주기마다 조회
    @Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void scheduledLookupReservation() {
        List<String> reserveIds = getAllReservedPayment();

        for (String id : reserveIds) {
            lookupReservation(id);
        }
    }

    //payment 상태 변경
    @Transactional(readOnly = false)
    public void lookupReservation(String id) {
        try {
            String status = bootPayService.reserveLookup(id);

            if (status.equals("1")) {
                Payment payment = getPaymentByReserveId(id);
                payment.updateSubscribtionPayment(PaymentStatus.COMPLETED, payment.getPaymentSubscribedAt());
                paymentRepository.save(payment);

                //정기결제 30일 후 새로운 정기결제 예약
                LocalDateTime newPaymentTime = payment.getPaymentSubscribedAt().plusDays(30);

                String newReserveId = bootPayService.reservePayment(payment.getBillingKey(), payment.getAmount(), newPaymentTime);
                PaymentReserve paymentReserve = payment.toPaymentReserve();
                paymentReserve.setPaymentSubscribedAt(newPaymentTime);
                createPayment(paymentReserve, newReserveId);
            } else if (status.equals("3")) {
                Payment payment = getPaymentByReserveId(id);
                payment.updateSubscribtionPayment(PaymentStatus.FAILED, payment.getPaymentSubscribedAt());
                paymentRepository.save(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
