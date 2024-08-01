package com.elice.nbbang.domain.payment.service;

import static org.hibernate.query.sqm.tree.SqmNode.log;

import com.elice.nbbang.domain.payment.entity.Payment;
import com.elice.nbbang.domain.payment.entity.enums.PaymentStatus;
import com.elice.nbbang.domain.payment.repository.PaymentRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoPaySchedulerService {

    private final PaymentRepository paymentRepository;
    private final KakaoPayService kakaopayService;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
//    @Scheduled(fixedRate = 60000)
    public void processRecurringPayments() {
        LocalDateTime today = LocalDateTime.of(2025, 8, 1, 0, 0, 0);

        // SUBSCRIBED 상태이고 nextPaymentDate가 오늘보다 이전인 Payment 조회
        List<Payment> paymentsDue = paymentRepository.findAllByStatusAndPaymentSubscribedAtBefore(
            PaymentStatus.SUBSCRIBED, today);

        for (Payment payment : paymentsDue) {
            try {
                kakaopayService.subscription(payment.getUser().getId(), payment.getOttId());
                log.info("Scheduled subscription for userId={}, ottId={} completed.");
                paymentRepository.save(payment);
            } catch (Exception e) {
                log.error("Error scheduling subscription for userId={}, ottId={}");
            }
        }
    }
}