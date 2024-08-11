package com.elice.nbbang.domain.payment.service;

import static com.elice.nbbang.global.exception.ErrorCode.PAYMENT_NOT_FOUND;
import static org.hibernate.query.sqm.tree.SqmNode.log;


import com.elice.nbbang.domain.payment.dto.PaymentDto;
import com.elice.nbbang.domain.payment.dto.PaymentRefundDTO;
import com.elice.nbbang.domain.payment.dto.PaymentReserve;
import com.elice.nbbang.domain.payment.entity.Card;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.entity.enums.PaymentType;
import com.elice.nbbang.domain.payment.repository.CardRepository;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import com.elice.nbbang.domain.user.entity.User;
import com.elice.nbbang.domain.user.repository.UserRepository;
import com.elice.nbbang.domain.user.service.UserUtilService;
import com.elice.nbbang.global.exception.CustomException;
import com.elice.nbbang.global.util.UserUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;
import java.util.Optional;
import com.elice.nbbang.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final UserUtilService userUtilService;
    private final CardRepository cardRepository;

    public static final int FEE = 500;
    public static final int SETTLEMENT_FEE = 200;

    /**
     * 모든 Payments 조회 page 적용
     */
    public Page<PaymentDto> getAllPayments(Pageable pageable) {
        return paymentRepository.findAllByOrderByPaymentCreatedAtDesc(pageable)
            .map(PaymentDto::fromEntity);
    }

    /**
     * userId로 Payments 조회
     */
    public Page<PaymentDto> getPaymentsByPartnerUserId(String partnerUserId, Pageable pageable) {
        return paymentRepository.findByPartnerUserIdContainingOrderByPaymentCreatedAtDesc(partnerUserId, pageable)
            .map(PaymentDto::fromEntity);
    }

    /**
     * TID로 Payments 조회
     */
    public Page<PaymentDto> getPaymentsByTid(String tid, Pageable pageable) {
        return paymentRepository.findByTidContainingOrderByPaymentCreatedAtDesc(tid, pageable)
            .map(PaymentDto::fromEntity);
    }

    /**
     * 상태별 Payments 조회
     */
    public Page<PaymentDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        return paymentRepository.findByStatusOrderByPaymentCreatedAtDesc(status, pageable)
            .map(PaymentDto::fromEntity);
    }

    /**
     * 환불 정보 반환 메소드
     */
    private PaymentRefundDTO calculateRefund(Payment payment) {
        int paymentAmount = payment.getAmount();
        double rawDayPrice = (double) paymentAmount / 30; // 일일 가격 계산 (소수점 포함)

        // 1일 이용금액 계산 (소수점 이하 절삭)
        int oneDayPrice = (int) Math.floor(rawDayPrice);

        LocalDate paymentApprovedDate = payment.getPaymentApprovedAt().toLocalDate();
        LocalDate currentDate = LocalDate.of(2024, 8, 30); // <<테스트날짜임
        long daysUsed = ChronoUnit.DAYS.between(paymentApprovedDate, currentDate);

        // 사용된 금액 계산 (소수점 이하 절삭)
        double rawAmountUsed = oneDayPrice * daysUsed + FEE;
        int amountUsed = (int) Math.floor(rawAmountUsed); // 소수점 이하 절삭

        // 환불 금액 계산 (음수가 되지 않도록 함)
        int refundAmount = Math.max(0, paymentAmount - amountUsed);

        return new PaymentRefundDTO(paymentAmount, refundAmount, oneDayPrice, paymentApprovedDate, currentDate, daysUsed, FEE, amountUsed);
    }

    /**
     * 환불 Info 조회
     */
    public PaymentRefundDTO getRefundInfo(Long userId, Long ottId) {
        Optional<Payment> paymentOptional = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);
        log.info("환불 정보 확인하는 메소드");
        log.info("userId: " + userId + ", ottId: " + ottId);

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            return calculateRefund(payment);
        } else {
            throw new CustomException(PAYMENT_NOT_FOUND);
        }
    }


    /**
     * 환불 신청 시 환불 금액, 결제 상태, 환불 요청일 업데이트 실제 환불이 진행되진 않음.
     * todo: 메소드 이름을 approveRefund로 바꾸고싶은데....
     */
    @Transactional(readOnly = false)
    public void getRefundAmount(Long userId, Long ottId) {
        Optional<Payment> paymentOptional = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);
        log.info("환불 시작합니다~~~");
        log.info("userId: " + userId + ", ottId: " + ottId);

        if (paymentOptional.isPresent()) {
            Payment payment = paymentOptional.get();
            PaymentRefundDTO refundDTO = calculateRefund(payment);

            // Payment 객체의 상태 업데이트
            payment.updateRefundPayment(PaymentStatus.REFUND_REQUESTED, refundDTO.getRefundAmount(), LocalDateTime.now());

            // 변경사항을 데이터베이스에 저장
            paymentRepository.save(payment);
        } else {
            throw new CustomException(PAYMENT_NOT_FOUND);
        }
    }

    /**
     * 재매칭 시 다음 결제일 수정 로직
     */
    @Transactional(readOnly = false)
    public void updatePaymentSubscribedAt(Long userId, Long ottId, int delayDate) {
        Optional<Payment> paymentOptional = paymentRepository.findTopByUserIdAndOttIdOrderByPaymentApprovedAtDesc(userId, ottId);

        Payment payment = paymentOptional.orElseThrow(() -> new CustomException(PAYMENT_NOT_FOUND));

        LocalDateTime currentSubscribedAt = payment.getPaymentSubscribedAt();

        // 현재 결제일에 delayDate만큼 더함
        LocalDateTime updatedSubscribedAt = currentSubscribedAt.plusDays(delayDate);

        // 수정된 결제일을 설정
        payment.updatePaymentSubscribedAt(updatedSubscribedAt);

        // 변경 사항 저장
        paymentRepository.save(payment);
    }

    //payment 생성
    @Transactional(readOnly = false)
    public Payment createPayment(PaymentReserve reserve, String reserveId, int amount) {
        Card card = cardRepository.findByUserId(reserve.getUser().getId())
            .orElseThrow(() -> new IllegalArgumentException("해당 유저의 카드 정보가 없습니다."));

        Payment payment = Payment.builder()
            .user(reserve.getUser())
            .cardCompany(card.getCardCompany())
            .billingKey(reserve.getBillingKey())
            .amount(amount)
            .ottId(reserve.getOtt().getId())
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
    public void deletePayment(String reserveId) {
        Payment payment = paymentRepository.findByReserveId(reserveId).orElse(null);
        Payment updatedPayment = payment.toBuilder()
            .status(PaymentStatus.RESERVE_CANCELLED)
            .build();
        paymentRepository.save(updatedPayment);
    }

    //payment 상태변경(결제 취소)
    @Transactional(readOnly = false)
    public void cancelPayment(String id, Double cancelAmount) {
        Payment payment = paymentRepository.findByReceiptId(id).orElse(null);
        Payment updatedPayment = payment.toBuilder()
            .status(PaymentStatus.REFUNDED_COMPLETED)
            .refundAmount(cancelAmount.intValue())
            .build();
        paymentRepository.save(updatedPayment);
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
