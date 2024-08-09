package com.elice.nbbang.domain.payment.service;

import static com.elice.nbbang.global.exception.ErrorCode.PAYMENT_NOT_FOUND;
import static org.hibernate.query.sqm.tree.SqmNode.log;

import com.elice.nbbang.domain.ott.entity.Ott;
import com.elice.nbbang.domain.ott.repository.OttRepository;
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
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final UserUtilService userUtilService;
    private final CardRepository cardRepository;

    public static final int FEE = 500;
    public static final int SETTLEMENT_FEE = 200;

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
            LocalDate currentDate = LocalDate.now(); // <<테스트날짜임 //현재 날짜를 환불 신청일로 간주
            long daysUsed = ChronoUnit.DAYS.between(paymentApprovedDate, currentDate);

            // 사용한 일수만큼의 금액을 계산하여 환불금액 계산 수수료도 더해서 차감
            double amountUsed = dayPrice * daysUsed + FEE;
            int refundAmount = (int) Math.max(0, paymentAmount - amountUsed); // 환불 금액이 음수가 되지 않도록 함.

            // Payment 객체의 상태 업데이트
            payment.updateRefundPayment(PaymentStatus.REFUND_REQUESTED, refundAmount, LocalDateTime.now());

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
