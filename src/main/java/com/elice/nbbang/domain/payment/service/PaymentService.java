package com.elice.nbbang.domain.payment.service;

import static org.hibernate.query.sqm.tree.SqmNode.log;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.dto.PaymentRefundDTO;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.global.util.UserUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
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
    private final UserUtil userUtil;
    private final UserRepository userRepository;

    private static final int FEE = 500;

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

    /**
     * 환불 신청 시 환불 금액, 결제 상태, 환불 요청일 업데이트 실제 환불이 진행되진 않음.
     */
    @Transactional(readOnly = false)
    public void getRefundAmount(Long userId, Long ottId) {
        Optional<Payment> paymentOptional = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);
        log.info("환불 시작합니다~~~");
        log.info("userId: " + userId + ", ottId: " + ottId);

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();

            // 결제 금액 가져오기
            int paymentAmount = payment.getAmount();
            double dayPrice = (double) paymentAmount / 30; // 일일 가격 계산

            // 결제 승인일로부터 환불 신청일까지의 일수 계산
            LocalDate paymentApprovedDate = payment.getPaymentApprovedAt().toLocalDate();
            LocalDate currentDate = LocalDate.of(2024, 8, 20); // <<테스트날짜임 //현재 날짜를 환불 신청일로 간주
            long daysUsed = ChronoUnit.DAYS.between(paymentApprovedDate, currentDate);

            // 사용한 일수만큼의 금액을 계산하여 환불금액 계산 수수료도 더해서 차감
            double amountUsed = dayPrice * daysUsed + FEE;
            int refundAmount = (int) Math.max(0, paymentAmount - amountUsed); // 환불 금액이 음수가 되지 않도록 함.

            // Payment 객체의 상태 업데이트
            payment.updateRefundPayment(PaymentStatus.REFUND_REQUESTED, refundAmount, LocalDateTime.now());

            // 변경사항을 데이터베이스에 저장
            paymentRepository.save(payment);
        } else {
            throw new NoSuchElementException("해당 사용자와 OTT ID에 대한 결제 내역이 없습니다.");
        }
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

    //payment 상태변경(예약 취소)
    @Transactional(readOnly = false)
    public void deletePayment(String id) {
        Payment payment = paymentRepository.findByReserveId(id).orElse(null);
        Payment updatedPayment = payment.toBuilder()
            .status(PaymentStatus.RESERVE_CANCELLED)
            .build();
        paymentRepository.save(updatedPayment);
    }

    //payment 상태변경(결제 취소)
    @Transactional(readOnly = false)
    public void cancelPayment(String id, Double amount) {
        Payment payment = paymentRepository.findByReceiptId(id).orElse(null);
        Payment updatedPayment = payment.toBuilder()
            .status(PaymentStatus.CANCELED)
            .refundAmount(amount.intValue())
            .refundDate(LocalDateTime.now())
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
    //@Scheduled(fixedRate = 60000)
    @Transactional(readOnly = false)
    public void scheduledLookupReservation() {
        List<String> reserveIds = getAllReservedPayment();

        for (String id : reserveIds) {
            lookupReservation(id);
        }
    }

    //정기결제 예약
    @Transactional(readOnly = false)
    public void lookupReservation(String id) {
        try {
            HashMap<String, Object> response = bootPayService.reserveLookup(id);

            if (response.get("status").toString().equals("1")) {
                Payment payment = getPaymentByReserveId(id);
                payment.updateCompletePayment(PaymentStatus.COMPLETED, response.get("receipt_id").toString());
                paymentRepository.save(payment);

                //정기결제 30일 후 새로운 정기결제 예약
                LocalDateTime newPaymentTime = payment.getPaymentSubscribedAt().plusDays(30);
                Payment newPayment = payment.toBuilder()
                    .paymentSubscribedAt(newPaymentTime)
                    .build();

                String newReserveId = bootPayService.reservePayment(payment.getBillingKey(), payment.getAmount(), newPaymentTime);
                PaymentReserve paymentReserve = newPayment.toPaymentReserve();
                createPayment(paymentReserve, newReserveId);
            } else if (response.get("status").toString().equals("3")) {
                Payment payment = getPaymentByReserveId(id);
                payment.updateSubscribtionPayment(PaymentStatus.FAILED, payment.getPaymentSubscribedAt());
                paymentRepository.save(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //userId 반환하는 메서드
    @Transactional(readOnly = false)
    public Long getAuthenticatedUserId() {
        String email = userUtil.getAuthenticatedUserEmail();
        if (email != null) {
            User user = userRepository.findByEmail(email);
            if (user != null) {
                return user.getId();
            } else {
                throw new NoSuchElementException("해당 이메일에 대한 사용자 정보를 찾을 수 없습니다.");
            }
        }
        throw new NoSuchElementException("인증된 사용자가 없습니다.");
    }
}
